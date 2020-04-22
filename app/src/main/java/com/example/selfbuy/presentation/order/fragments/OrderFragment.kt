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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.resumeOrderList.SFResumeOrderListAdapter
import com.example.selfbuy.data.entity.remote.*
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.home.activity.HomeActivity
import com.example.selfbuy.presentation.order.viewModel.CreditCardViewModel
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageStepOrder
import com.example.selfbuy.utils.ManageThread
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.StripeIntent
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.webview_3d_secure.*

class OrderFragment(private val selectedCreditCardId: String) : Fragment() {

    private val resumeOrderListAdapter = SFResumeOrderListAdapter()
    private val creditCardViewModel = CreditCardViewModel()
    private var pageChanged = 0
    private var orderEnded = false
    private var clientSecretOrder = ""
    private var count3dSecureAction = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_order, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ManageStepOrder.nextStep(this.activity as AppCompatActivity)

        resume_order_recycle_view.apply { getCartProduct(this) }

        this.setBtnOnClickListener()
        this.bindCreateCardViewModel()
    }

    /**
     * Evenement du click sur le bouton de validation de commande pour payer
     */
    private fun setBtnOnClickListener() {
        btn_resume_order_next.setOnClickListener {
            progressBar_resume_order.visibility = View.VISIBLE
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
                    val orderDto = OrderDto(finalListeDetailOrderDto, selectedCreditCardId)
                    creditCardViewModel.createPaymentIntent(orderDto)
                }
            }.execute()
        }
    }

    /**
     * On s'abonne aux differents evenements de CreditCardViewModel
     */
    private fun bindCreateCardViewModel() {
        creditCardViewModel.paymentIntentLiveData.observe(
            viewLifecycleOwner,
            Observer { resultDto: ResultApiDto<PaymentIntentDto> ->
                progressBar_resume_order.visibility = View.GONE
                if (resultDto.data != null) {
                    clientSecretOrder = resultDto.data.clientSecret

                    isStatusPaymentValide()
                }
            })

        creditCardViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_resume_order.visibility = View.GONE
            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_SHORT).show() }
        })
    }

    /**
     * Recuperer les produits dans la base room pour pouvoir les afficher dans le panier
     */
    private fun getCartProduct(recycleView: RecyclerView) {
        resume_order_recycle_view.layoutManager = LinearLayoutManager(this.context)

        Async {
            val resultQuery = SFApplication.app.dbRoom.productDao().getAll()

            ManageThread.executeOnMainThread(this.context!!){
                loadDataInRecycleView(resultQuery, recycleView)
            }
        }.execute()
    }

    /**
     * Charge les données dans la recycleview
     */
    private fun loadDataInRecycleView(productList: List<Product>, recycleView: RecyclerView){
        resumeOrderListAdapter.updateList(productList)
        recycleView.adapter = resumeOrderListAdapter

        var totalPrice = 0.0
        productList.forEach { product: Product ->
            totalPrice += product.price * product.quantity
        }

        val totalPriceToDisplay = "${"%.2f".format(totalPrice)}${getString(R.string.euro_symbol)}"
        tw_resume_order_total.text = totalPriceToDisplay
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
                        replace(R.id.order_activity_fragment_container, orderConfirmed)
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
                            val map = it.value as Map<*, *>?

                            map?.forEach{ it1 ->
                                if(it1.key == "stripe_js"){
                                    if (it1.value != null && it1.value is String) {
                                        webview_3d_secure_include.visibility = View.VISIBLE
                                        loadWebView3DSecure(it1.value!!.toString())
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

                    val intent = Intent(this@OrderFragment.activity, HomeActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(this@OrderFragment.activity, R.string.error_during_payment, Toast.LENGTH_LONG).show()
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