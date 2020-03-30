package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200 || response.code() == 204) {
                        refreshUserLiveDate.postValue(true)
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