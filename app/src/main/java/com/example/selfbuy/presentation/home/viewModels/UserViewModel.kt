package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.entity.remote.User
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserViewModel: ViewModel() {

    val userLiveData: MutableLiveData<ResultApi<User>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
    Permet à un utilisateur de se connecter
     */
    @SuppressLint("CheckResult")
    fun getCurrentUser() {
        SFApplication
            .app
            .userRepository
            .getCurrentUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
}