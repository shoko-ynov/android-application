package com.example.selfbuy.presentation.order.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.selfbuy.R
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageThread
import kotlinx.android.synthetic.main.fragment_select_credit_card.*

class SelectCreditCardFragment : Fragment() {

    private lateinit var creditCards: Array<String>
    private var selectedCreditCard: String = ""

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

        this.getTotalPrices()
        this.initSpinnerCreditCards(view)
        this.spinnerItemOnClickListener()
    }

    /**
     * Initialise le spinner des cartes de credits de l'utilisateur connecté
     */
    private fun initSpinnerCreditCards(view: View){
        creditCards = view.resources.getStringArray(R.array.credit_cards_array)
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, creditCards)
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner_credit_cards.adapter = dataAdapter
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
                selectedCreditCard = creditCards[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                selectedCreditCard = creditCards[0]
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
}
