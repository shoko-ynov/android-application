package com.example.selfbuy.presentation.order.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.order.viewModel.CreditCardViewModel
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageThread
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_select_credit_card.*

class SelectCreditCardFragment : Fragment() {

    private val creditCards : MutableList<String> = arrayListOf()
    private var selectedCreditCard : CreditCardDto? = null
    private var listCreditCardUser : ArrayList<CreditCardDto> = arrayListOf()
    private val creditCardViewModel = CreditCardViewModel()
    private var isFirstLoad : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =inflater.inflate(R.layout.fragment_select_credit_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_confirm_command.setOnClickListener {
            val orderConfirmed = OrderConfirmedFragment()
            val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction().apply {
                replace(R.id.select_credit_card_activity_fragment_container, orderConfirmed)
                addToBackStack(null)
            }
            fragmentTransaction.commit()
        }

        this.bindCreateCardViewModel()
        progressBar_list_cards_user.visibility = View.VISIBLE
        this.creditCardViewModel.getUserCards()
        this.getTotalPrices()
        this.spinnerItemOnClickListener()
        isFirstLoad = false
    }

    /**
     * Abonnement aux evenements de selection du spinner des cartes de credits
     */
    private fun spinnerItemOnClickListener(){
        spinner_credit_cards.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if (isFirstLoad && listCreditCardUser.size>0)
                {
                    selectedCreditCard = listCreditCardUser.find { it.isDefaultCard }
                }
                else
                {
                    selectedCreditCard = listCreditCardUser[position]
                    view?.let { v -> Snackbar.make(v, selectedCreditCard!!._id, Snackbar.LENGTH_SHORT).show() }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedCreditCard = listCreditCardUser[0]
            }
        }
    }

    /**
     * Recupere le prix total du panier de l'utilisateur
     */
    private fun getTotalPrices(){
        Async {
            val resultQuery = SFApplication.app.dbRoom.productDao().getAll()

            ManageThread.executeOnMainThread(this.context!!){
                var totalPrice = 0.0
                resultQuery.forEach { product: Product ->
                    totalPrice += product.price * product.quantity
                }

                val totalPriceToDisplay = "${"%.2f".format(totalPrice)}${getString(R.string.euro_symbol)}"
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

                if (resultDto.data != null)
                {
                    listCreditCardUser = resultDto.data
                    resultDto.data.forEach{
                        creditCards.add("**** **** **** ${it.last4}           ${it.expMonth}/${it.expYear}")
                    }
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, creditCards)
                    dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                    spinner_credit_cards.adapter = dataAdapter
                }
            })

        creditCardViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_list_cards_user.visibility = View.GONE
            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_SHORT).show() }
        })
    }
}
