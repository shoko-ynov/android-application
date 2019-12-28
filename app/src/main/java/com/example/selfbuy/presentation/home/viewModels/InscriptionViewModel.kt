package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.local.Inscription
import com.example.selfbuy.data.entity.remote.InscriptionDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InscriptionViewModel: ViewModel() {

    val userLiveData: MutableLiveData<ResultApiDto<InscriptionDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
    Permet à un utilisateur de se connecter
     */
    @SuppressLint("CheckResult")
    fun inscription(inscription: Inscription) {
        SFApplication
            .app
            .inscriptionRepository
            .inscription(inscription)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
}