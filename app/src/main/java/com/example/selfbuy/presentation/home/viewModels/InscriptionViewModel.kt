package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.local.Inscription
import com.example.selfbuy.data.entity.remote.InscriptionDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InscriptionViewModel: ViewModel() {

    val userRegisterLiveData: MutableLiveData<ResultApiDto<InscriptionDto>> = MutableLiveData()
    val errorRegisterLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
     *  Permet l'inscription d'un utilisateur avec l'email passé en parametre
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
                userRegisterLiveData.postValue(it)
            }, {e ->
                errorRegisterLiveData.postValue(e)
            })
    }
}