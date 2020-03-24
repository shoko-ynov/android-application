package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class UserViewModel: ViewModel() {

    val userDtoLiveData: MutableLiveData<ResultApiDto<UserDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    val refreshUserLiveDate: MutableLiveData<Boolean> = MutableLiveData()

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
                userDtoLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }

    /**
     * Route pour la mise à jour du profile utilisateur
     */
    @SuppressLint("CheckResult")
    fun putUserById(user: UserDto) {
        SFApplication
            .app
            .userRepository
            .putUserById(user._id, user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                refreshUserLiveDate.postValue(it)
            }, {e ->
                refreshUserLiveDate.postValue(true)
            })
    }
}