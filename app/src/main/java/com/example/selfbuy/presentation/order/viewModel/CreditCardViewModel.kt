package com.example.selfbuy.presentation.order.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.example.selfbuy.data.entity.remote.OrderDto
import com.example.selfbuy.data.entity.remote.PaymentIntentDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CreditCardViewModel: ViewModel() {

    val creditCardsLiveData: MutableLiveData<ResultApiDto<ArrayList<CreditCardDto>>> = MutableLiveData()
    val paymentIntentLiveData: MutableLiveData<ResultApiDto<PaymentIntentDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
     * Permet de recuperer la liste des cartes de crédit de l'utilisateur actif
     */
    @SuppressLint("CheckResult")
    fun getUserCards() {
        SFApplication
            .app
            .paymentRepository
            .getUserCards()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                creditCardsLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
    /**
     * Permet de créer le paiement
     */
    @SuppressLint("CheckResult")
    fun createPaymentIntent(orderDto : OrderDto) {
        SFApplication
            .app
            .paymentRepository
            .createPaymentIntent(orderDto)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                paymentIntentLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }


}