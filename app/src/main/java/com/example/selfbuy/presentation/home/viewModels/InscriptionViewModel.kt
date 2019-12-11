package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.local.InscriptionDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class InscriptionViewModel: ViewModel() {

    val userLiveData: MutableLiveData<ResultApiDto<TokenDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
    Permet à un utilisateur de se connecter
     */
    @SuppressLint("CheckResult")
    fun inscription(inscriptionDto: InscriptionDto) {
        SFApplication
            .app
            .inscriptionRepository
            .inscription(inscriptionDto)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
}