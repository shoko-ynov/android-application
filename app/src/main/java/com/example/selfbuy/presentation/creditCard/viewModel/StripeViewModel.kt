package com.example.selfbuy.presentation.creditCard.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.StripeDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StripeViewModel: ViewModel() {

    val stripeDtoLiveData: MutableLiveData<ResultApiDto<CreditCardDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
     * Route permettant de link une carte banquaire a l'utilisateur actif
     */
    @SuppressLint("CheckResult")
    fun linkCardToUser(stripeToken: StripeDto) {
        SFApplication
            .app
            .paymentRepository
            .linkCardToUser(stripeToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                stripeDtoLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
}