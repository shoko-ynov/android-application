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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreditCardViewModel: ViewModel() {

    val creditCardsLiveData: MutableLiveData<ResultApiDto<ArrayList<CreditCardDto>>> = MutableLiveData()
    val paymentIntentLiveData: MutableLiveData<ResultApiDto<PaymentIntentDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    val deleteCreditCard: MutableLiveData<Boolean> = MutableLiveData()

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

    /**
     * Route permettant de supprimer une carte bancaire
     */
    @SuppressLint("CheckResult")
    fun deleteCreditCard(cardId: String) {
        SFApplication
            .app
            .paymentRepository
            .deleteCreditCard(cardId)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200 || response.code() == 204) {
                        deleteCreditCard.postValue(true)
                    }
                    else{
                        errorLiveData.postValue(Throwable(response.code().toString()))
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    errorLiveData.postValue(t)
                }
            })
    }
}