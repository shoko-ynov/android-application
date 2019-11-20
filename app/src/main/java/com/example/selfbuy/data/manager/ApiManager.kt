package com.example.selfbuy.data.manager

import com.example.selfbuy.data.entity.remote.Token
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.manager.api.BaseApi.API_BASE_URL
import com.example.selfbuy.data.manager.service.ApiService
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {

    private val service: ApiService = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)

    /**
     *  Permet de se connecter et retourne l'utilisateur connecté si il n'y a pas d'erreur
     */
    fun authenticate(loginDto: LoginDto): Single<ResultApi<Token>> = service.authenticate(loginDto)
}