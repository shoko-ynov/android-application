package com.example.selfbuy.data.manager.service

import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.local.Login
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.UserDto
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /**
     *  Route pour l'authentification
     */
    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("auth")
    fun authenticate(@Body login: Login) : Single<ResultApiDto<TokenDto>>

    /**
     * Route pour le refreshToken
     */
    @FormUrlEncoded
    @POST("auth/refresh")
    fun refreshToken(@Field("refreshToken") refreshToken: String?) : Call<ResultApiDto<TokenDto>>

    /**
     * Route permettant de récupérer les informations de l'utilisateur connecté en fonction de son token
     */
    @GET("me")
    fun getCurrentUser() : Single<ResultApiDto<UserDto>>

    /**
     * Route permettant de récupérer la liste de produit
     */
    @GET("products")
    fun getProducts() : Single<ResultApiDto<ArrayList<ProductDto>>>
}