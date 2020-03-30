package com.example.selfbuy.presentation.creditCard.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.StripeDto
import com.example.selfbuy.presentation.SFApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StripeViewModel: ViewModel() {

    val stripeDtoLiveData: MutableLiveData<Boolean> = MutableLiveData()
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
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200 || response.code() == 204) {
                        stripeDtoLiveData.postValue(true)
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