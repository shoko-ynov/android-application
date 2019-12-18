package com.example.selfbuy.data.repository.product

import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single

class ProductRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     * Route permettant de récupérer la liste de produit
     */
    fun getProducts(): Single<ResultApiDto<ArrayList<ProductDto>>> = apiManager.getProducts()

    /**
     * Route permettant de récupérer le détail d'un produit
     */
    fun getProductById(idProduct : String) : Single<ResultApiDto<ProductDto>> = apiManager.getProductById(idProduct)
}