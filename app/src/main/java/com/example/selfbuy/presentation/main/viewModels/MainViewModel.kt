package com.example.selfbuy.presentation.main.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainViewModel: ViewModel() {

    val errorLiveData:  MutableLiveData<Throwable> = MutableLiveData()

    init {

    }
}