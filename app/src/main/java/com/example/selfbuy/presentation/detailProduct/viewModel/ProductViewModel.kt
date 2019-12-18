package com.example.selfbuy.presentation.detailProduct.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.presentation.SFApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ProductViewModel: ViewModel() {

    val productLiveData: MutableLiveData<ResultApiDto<ProductDto>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    /**
     * Permet de recuperer le détail d'un produit
     */
    @SuppressLint("CheckResult")
    fun getProductById(idProduct : String) {
        SFApplication
            .app
            .productRepository
            .getProductById(idProduct)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                productLiveData.postValue(it)
            }, {e ->
                errorLiveData.postValue(e)
            })
    }
}