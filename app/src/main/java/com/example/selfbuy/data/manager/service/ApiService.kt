package com.example.selfbuy.data.manager.service

import com.example.selfbuy.data.entity.remote.Token
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.entity.remote.User
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /**
     *  Route pour l'authentification
     */
    @POST("auth")
    fun authenticate(@Body loginDto: LoginDto) : Single<ResultApi<Token>>

    /**
     * Route pour le refreshToken
     */
    @FormUrlEncoded
    @POST("auth/refresh")
    fun refreshToken(@Field("refreshToken") refreshToken: String?) : Call<ResultApi<Token>>

    /**
     * Route permettant de récupérer les informations de l'utilisateur connecté en fonction de son token
     */
    @GET("me")
    fun getCurrentUser() : Single<ResultApi<User>>
}