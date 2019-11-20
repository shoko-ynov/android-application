package com.example.selfbuy.presentation.home.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class HomeViewModel: ViewModel() {

    val errorLiveData:  MutableLiveData<Throwable> = MutableLiveData()

    init {

    }
}