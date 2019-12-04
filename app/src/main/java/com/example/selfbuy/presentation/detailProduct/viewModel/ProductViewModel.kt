package com.example.selfbuy.presentation.home.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel: ViewModel() {

    val productLiveData: MutableLiveData<ResultApiDto<ArrayList<ProductDto>>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
     * Permet de recuperer la liste des produits
     */
    @SuppressLint("CheckResult")
    fun getProducts() {
        SFApplication
            .app
            .productRepository
            .getProducts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                productLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
}