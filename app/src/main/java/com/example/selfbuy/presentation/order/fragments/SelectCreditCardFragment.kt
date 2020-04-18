package com.example.selfbuy.presentation.order.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.StripeIntent
import kotlinx.android.synthetic.main.fragment_select_credit_card.*
import kotlinx.android.synthetic.main.webview_3d_secure.*

class SelectCreditCardFragment : Fragment() {

    private val creditCards: MutableList<String> = arrayListOf()
    private var selectedCreditCard: CreditCardDto? = null
    private var listCreditCardUser: ArrayList<CreditCardDto> = arrayListOf()
    private val creditCardViewModel = CreditCardViewModel()
    private var isFirstLoad: Boolean = true
    private var pageChanged = 0
    private var orderEnded = false
    private var clientSecretOrder = ""
    private var count3dSecureAction = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_select_credit_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.btnConfirmClickListener()
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
     * Evenement du click sur le bouton de confirmation de commande
     */
    private fun btnConfirmClickListener() {
        btn_confirm_command.setOnClickListener {
            if (selectedCreditCard != null) {
                progressBar_list_cards_user.visibility = View.VISIBLE
                Async {
                    val resultQuery = SFApplication.app.dbRoom.productDao().getAll()
                    ManageThread.executeOnMainThread(this.context!!) {
                        val listDetailOrderDto: MutableList<DetailOrderDto> = arrayListOf()
                        resultQuery.forEach {
                            val detailOrderDto = DetailOrderDto(it.uid, it.quantity)
                            listDetailOrderDto.add(detailOrderDto)
                        }
                        val finalListeDetailOrderDto: ArrayList<DetailOrderDto> = arrayListOf()
                        finalListeDetailOrderDto.addAll(listDetailOrderDto)
                        val orderDto = OrderDto(finalListeDetailOrderDto, selectedCreditCard!!._id)
                        creditCardViewModel.createPaymentIntent(orderDto)
                    }
                }.execute()
            }
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
                if (isFirstLoad && listCreditCardUser.size > 0) {
                    selectedCreditCard = listCreditCardUser.find { it.isDefaultCard }
                } else {
                    selectedCreditCard = listCreditCardUser[position]
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
                }
            })

        creditCardViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_list_cards_user.visibility = View.GONE
            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_SHORT).show() }
        })

        creditCardViewModel.paymentIntentLiveData.observe(
            viewLifecycleOwner,
            Observer { resultDto: ResultApiDto<PaymentIntentDto> ->
                 progressBar_list_cards_user.visibility = View.GONE
                    if (resultDto.data != null) {
                        clientSecretOrder = resultDto.data.clientSecret

                        isStatusPaymentValide()
                    }
                })
    }

    private fun isStatusPaymentValide(){
        val stripe = Stripe(
            this.context!!,
            PaymentConfiguration.getInstance(this.context!!).publishableKey
        )

        // Disable the `NetworkOnMainThreadException` and make sure it is just logged.
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())

        val result =
            stripe.retrievePaymentIntentSynchronous(clientSecretOrder)

        ManageThread.executeOnMainThread(this.context!!){
            if (result != null)
            {
                if(result.status == StripeIntent.Status.Succeeded && result.nextAction == null)
                {
                    val orderConfirmed = OrderConfirmedFragment()
                    val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction().apply {
                        replace(R.id.select_credit_card_activity_fragment_container, orderConfirmed)
                        addToBackStack(null)
                    }
                    fragmentTransaction.commit()
                }
                else
                {
                    //WebView 3D secure
                    count3dSecureAction++
                    result.nextAction?.forEach {
                        if(it.key == "use_stripe_sdk"){
                            val map = it.value as Map<String, String?>?

                            map?.forEach{ it1 ->
                                if(it1.key == "stripe_js"){
                                    if (it1.value != null) {
                                        webview_3d_secure_include.visibility = View.VISIBLE
                                        loadWebView3DSecure(it1.value!!)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Charge la webview pour le 3D secure
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView3DSecure(url:String){
        webview_3d_secure.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                if(count3dSecureAction > 4 && !orderEnded){
                    orderEnded = true

                    val intent = Intent(this@SelectCreditCardFragment.activity, HomeActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this@SelectCreditCardFragment.activity, R.string.error_during_payment, Toast.LENGTH_LONG).show()
                }
                else{
                    pageChanged++

                    if(pageChanged == 5 && !orderEnded){
                        orderEnded = true
                        pageChanged = 0

                        // Make sure you remove the WebView from its parent view before doing anything.
                        webview_3d_secure.removeAllViews()
                        webview_3d_secure.clearHistory()

                        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
                        // Probably not a great idea to pass true if you have other WebViews still alive.
                        webview_3d_secure .clearCache(true)

                        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
                        webview_3d_secure.loadUrl("about:blank")

                        webview_3d_secure_include.visibility = View.GONE

                        isStatusPaymentValide()
                    }
                }
            }
        }
        webview_3d_secure.settings.javaScriptCanOpenWindowsAutomatically = true
        webview_3d_secure.settings.javaScriptEnabled = true
        webview_3d_secure.settings.allowContentAccess = true

        orderEnded = false
        webview_3d_secure.loadUrl(url)
    }
}
