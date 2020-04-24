package com.example.selfbuy.presentation.order.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.*
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.creditCard.viewModel.StripeViewModel
import com.example.selfbuy.presentation.order.activity.OrderActivity
import com.example.selfbuy.presentation.order.viewModel.CreditCardViewModel
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageStepOrder
import com.example.selfbuy.utils.ManageThread
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.fragment_credit_card.*
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_select_credit_card.*

enum class TypeCardToSave{
    Nothing,
    NewCard,
    InListCard
}

enum class CardUserEnum{
    Nothing,
    Yes,
    No
}

object ManageCardUser{
    var linkCardToUser: CardUserEnum = CardUserEnum.Nothing
    var selectedCardId: String? = null
}

class SelectCreditCardFragment : Fragment() {

    private val creditCards: MutableList<String> = arrayListOf()
    private var selectedCreditCard: CreditCardDto? = null
    private var listCreditCardUser: ArrayList<CreditCardDto> = arrayListOf()
    private val creditCardViewModel = CreditCardViewModel()
    private var isFirstLoad: Boolean = true
    private var typeCard: TypeCardToSave =  TypeCardToSave.NewCard
    private val stripeViewModel = StripeViewModel()
    private var cardId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_select_credit_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(this.activity is BaseActivity){
            val baseActivity = activity as BaseActivity
            baseActivity.supportActionBar(true)
            baseActivity.setTitle(R.string.payement_method)

            ManageStepOrder.initStep(baseActivity)
        }

        btn_confirm_command.setOnClickListener {
            if(typeCard == TypeCardToSave.Nothing){
                Snackbar.make(it, R.string.select_payment_method, Snackbar.LENGTH_SHORT).show()
            }
            else{
                if(selectedCreditCard != null && typeCard == TypeCardToSave.InListCard){
                    progressBar_list_cards_user.visibility = View.VISIBLE
                    val intent = Intent(this.context, OrderActivity::class.java)
                    intent.putExtra("selectedCreditCard", selectedCreditCard!!._id)

                    Handler().postDelayed({
                        progressBar_list_cards_user.visibility = View.GONE
                        startActivity(intent)

                    }, 1000)
                }
                else if(typeCard == TypeCardToSave.NewCard){
                    this.createPaymentIntentWithNewCard()
                }
            }
        }

        this.bindCreateCardViewModel()
        this.bindStripeViewModel()
        progressBar_list_cards_user.visibility = View.VISIBLE
        this.creditCardViewModel.getUserCards()
        this.getTotalPrices()
        this.spinnerItemOnClickListener()
        isFirstLoad = false

        tw_choose_payement_select_credit_card.setOnClickListener {
            if(spinner_credit_cards.visibility == Spinner.GONE){
                spinner_credit_cards.visibility = Spinner.VISIBLE
                tw_choose_payement_select_credit_card.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_black_24dp, 0)

                include_enter_credit_card.visibility = View.GONE
                tw_enter_payement_credit_card.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0)

                typeCard = TypeCardToSave.InListCard
            }
            else{
                spinner_credit_cards.visibility = Spinner.GONE
                tw_choose_payement_select_credit_card.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0)

                typeCard = TypeCardToSave.Nothing
            }
        }

        tw_enter_payement_credit_card.setOnClickListener {
            if(include_enter_credit_card.visibility == View.GONE){
                include_enter_credit_card.visibility = View.VISIBLE
                tw_enter_payement_credit_card.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_up_black_24dp, 0)

                spinner_credit_cards.visibility = Spinner.GONE
                tw_choose_payement_select_credit_card.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0)

                typeCard = TypeCardToSave.NewCard
            }
            else{
                include_enter_credit_card.visibility = View.GONE
                tw_enter_payement_credit_card.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_black_24dp, 0)

                typeCard = TypeCardToSave.Nothing
            }
        }

        cardInputWidget.setShouldShowPostalCode(false)
    }

    override fun onResume() {
        super.onResume()

        if(ManageCardUser.linkCardToUser == CardUserEnum.No && !ManageCardUser.selectedCardId.isNullOrEmpty()){
            progressBar_list_cards_user.visibility = View.VISIBLE
            creditCardViewModel.deleteCreditCard(ManageCardUser.selectedCardId!!)

            ManageCardUser.linkCardToUser = CardUserEnum.Nothing
            ManageCardUser.selectedCardId = null
        }

        if(!isFirstLoad){
            progressBar_list_cards_user.visibility = View.VISIBLE
            this.creditCardViewModel.getUserCards()
        }
    }

    /**
     * Abonnement aux evenements de selection du spinner des cartes de credits
     */
    private fun spinnerItemOnClickListener() {
        spinner_credit_cards.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                selectedCreditCard = listCreditCardUser[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedCreditCard = listCreditCardUser[0]
            }
        }
    }

    /**
     * Recupere le prix total du panier de l'utilisateur
     */
    private fun getTotalPrices() {
        Async {
            val resultQuery = SFApplication.app.dbRoom.productDao().getAll()

            ManageThread.executeOnMainThread(this.context!!) {
                var totalPrice = 0.0
                resultQuery.forEach { product: Product ->
                    totalPrice += product.price * product.quantity
                }

                val totalPriceToDisplay =
                    "${"%.2f".format(totalPrice)}${getString(R.string.euro_symbol)}"
                tw_total_select_credit_card.text = totalPriceToDisplay
            }
        }.execute()
    }

    /**
     * On s'abonne aux differents evenements de CreditCardViewModel
     */
    private fun bindCreateCardViewModel() {
        creditCardViewModel.creditCardsLiveData.observe(
            viewLifecycleOwner,
            Observer { resultDto: ResultApiDto<ArrayList<CreditCardDto>> ->
                progressBar_list_cards_user.visibility = View.GONE

                if (resultDto.data != null) {
                    creditCards.clear()

                    listCreditCardUser = resultDto.data
                    resultDto.data.forEach {
                        var defaultCard = ""
                        if(it.isDefaultCard){
                            defaultCard = "-  ${getString(R.string.default_card)}"
                        }
                        creditCards.add("**** **** **** ${it.last4}  -  ${it.expMonth}/${it.expYear}  $defaultCard")
                    }
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
                        context!!,
                        R.layout.support_simple_spinner_dropdown_item,
                        creditCards
                    )
                    dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                    spinner_credit_cards.adapter = dataAdapter

                    if(selectedCreditCard != null){
                        val indexSelectedCreditCard = listCreditCardUser.indexOf(selectedCreditCard!!)
                        spinner_credit_cards.setSelection(indexSelectedCreditCard)
                    }
                    else{
                        val defaultCreditCard = listCreditCardUser.find { it.isDefaultCard }
                        val indexDefaultCreditCard = listCreditCardUser.indexOf(defaultCreditCard)
                        spinner_credit_cards.setSelection(indexDefaultCreditCard)
                    }
                }
            })

        creditCardViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_list_cards_user.visibility = View.GONE
            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_SHORT).show() }
        })

        creditCardViewModel.deleteCreditCard.observe(viewLifecycleOwner, Observer {
            progressBar_list_cards_user.visibility = View.GONE
        })
    }

    private fun createPaymentIntentWithNewCard(){
        val params = cardInputWidget.paymentMethodCreateParams
        if(params!= null){
            val builder = AlertDialog.Builder(this.context!!)

            with(builder)
            {
                setTitle(R.string.save_credit_card)
                setMessage(R.string.want_to_save_credit_card)

                builder.setNeutralButton(
                    getString(R.string.yes)
                ) { _, _ ->
                    //On enregistre et on creer l'intent pour recuperer ensuite la carte et l'envoyer au prochain fragment
                    this@SelectCreditCardFragment.getCardFromStripeWidget()
                    ManageCardUser.linkCardToUser = CardUserEnum.Yes
                }
                builder.setNegativeButton(
                    getString(R.string.no)
                ) { _, _ ->
                    //On n'enregistre pas et on creer l'intent pour recuperer ensuite la carte et l'envoyer au prochain fragment
                    ManageCardUser.linkCardToUser = CardUserEnum.No
                    this@SelectCreditCardFragment.getCardFromStripeWidget()
                }
                show()
            }
        }
    }

    private fun getCardFromStripeWidget(){
        val params = cardInputWidget.paymentMethodCreateParams
        if (params != null) {
            progressBar_list_cards_user.visibility = View.VISIBLE

            val cardValues = params.toParamMap().entries.find {
                it.key == "card"
            }
            val creditCardValues = cardValues?.value as Map<*, *>

            var cvc = ""
            var expMonth = ""
            var expYear = ""
            var number = ""

            for ((key, value) in creditCardValues) {
                when (key) {
                    "cvc" -> cvc = value.toString()
                    "exp_month" -> expMonth = value.toString()
                    "exp_year" -> expYear = value.toString()
                    "number" -> number = value.toString()
                }
            }

            val stripe = Stripe(
                this.context!!,
                PaymentConfiguration.getInstance(this.context!!).publishableKey
            )
            val card: Card = Card.create(number, expMonth.toInt(), expYear.toInt(), cvc)

            val jsonCard =
                "{\"number\": ${card.number}, \"expMonth\": ${card.expMonth}, \"expYear\": ${card.expYear}, \"name\": \"${editext_name_credit_card.text.trim()}\", \"cvc\": ${card.cvc}, \"tokenType\": \"card\"}"

            val cardString = Gson().fromJson(jsonCard, Card::class.java)

            if (cardString != null) {
                stripe.createCardToken(
                    cardString,
                    callback = object : ApiResultCallback<Token> {
                        override fun onError(e: Exception) {
                            progressBar_add_card.visibility = View.GONE

                            val errorMessage = e.localizedMessage
                            if(!errorMessage.isNullOrEmpty()){
                                view?.let { v -> Snackbar.make(v, errorMessage, Snackbar.LENGTH_LONG).show() }
                            }
                        }

                        override fun onSuccess(result: Token) {
                            stripeViewModel.linkCardToUser(StripeDto(result.id))
                        }
                    }
                )
            }
        }
    }

    private fun bindStripeViewModel(){
        stripeViewModel.stripeDtoLiveData.observe(viewLifecycleOwner, Observer {
            ManageCardUser.selectedCardId = it.data!!._id

            if(!it.data._id.isNullOrEmpty()){
                cardId = it.data._id
            }
            this.redirectToOrderActivity()
        })

        stripeViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_add_card.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    private fun redirectToOrderActivity(){
        if(cardId.isNotEmpty()){
            val intent = Intent(this@SelectCreditCardFragment.context, OrderActivity::class.java)
            intent.putExtra("selectedCreditCard", cardId)

            Handler().postDelayed({
                progressBar_list_cards_user.visibility = View.GONE
                startActivity(intent)

            }, 1000)
        }
    }
}
