package com.example.selfbuy.presentation.order.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.*
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.creditCard.activity.CreditCardActivity
import com.example.selfbuy.presentation.home.activity.HomeActivity
import com.example.selfbuy.presentation.order.activity.OrderActivity
import com.example.selfbuy.presentation.order.viewModel.CreditCardViewModel
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageThread
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_select_credit_card.*
import kotlinx.android.synthetic.main.product_cart_cell_layout.*

class SelectCreditCardFragment : Fragment() {

    private val creditCards: MutableList<String> = arrayListOf()
    private var selectedCreditCard: CreditCardDto? = null
    private var listCreditCardUser: ArrayList<CreditCardDto> = arrayListOf()
    private val creditCardViewModel = CreditCardViewModel()
    private var isFirstLoad: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_select_credit_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(this.activity is AppCompatActivity){
            val myActivity = this.activity as AppCompatActivity
            myActivity.setTitle(R.string.payement_method)
        }

        btn_confirm_command.setOnClickListener {
            if(selectedCreditCard != null){
                progressBar_list_cards_user.visibility = View.VISIBLE
                val intent = Intent(this.context, OrderActivity::class.java)
                intent.putExtra("selectedCreditCard", selectedCreditCard!!._id)

                Handler().postDelayed({
                    progressBar_list_cards_user.visibility = View.GONE
                    startActivity(intent)

                }, 1000)
            }
        }

        this.btnAddCreditCardCommandClickListener()
        this.bindCreateCardViewModel()
        progressBar_list_cards_user.visibility = View.VISIBLE
        this.creditCardViewModel.getUserCards()
        this.getTotalPrices()
        this.spinnerItemOnClickListener()
        isFirstLoad = false
    }

    override fun onResume() {
        super.onResume()

        if(!isFirstLoad){
            progressBar_list_cards_user.visibility = View.VISIBLE
            this.creditCardViewModel.getUserCards()
        }
    }

    /**
     * Evenement du click sur le bouton d'ajout de carte de credit
     */
    private fun btnAddCreditCardCommandClickListener(){
        btn_add_credit_card_command.setOnClickListener {
            val intent = Intent(this.context, CreditCardActivity::class.java)
            startActivity(intent)
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
    }
}
