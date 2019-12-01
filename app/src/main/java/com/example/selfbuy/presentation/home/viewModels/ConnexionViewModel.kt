package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.local.Login
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ConnexionViewModel: ViewModel() {

    val tokenDtoLiveData: MutableLiveData<ResultApiDto<TokenDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
    Permet à un utilisateur de se connecter
     */
    @SuppressLint("CheckResult")
    fun authenticate(login: Login) {
        SFApplication
            .app
            .connexionRepository
            .authenticate(login)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                tokenDtoLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
}