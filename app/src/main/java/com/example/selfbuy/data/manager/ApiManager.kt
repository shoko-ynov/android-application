package com.example.selfbuy.data.manager

import com.example.selfbuy.data.entity.remote.Token
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.entity.remote.User
import com.example.selfbuy.data.manager.api.BaseApi.API_BASE_URL
import com.example.selfbuy.data.manager.service.InterceptorService
import com.example.selfbuy.data.manager.service.ApiService
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiManager {

    private var client = OkHttpClient.Builder()
        .addInterceptor(InterceptorService())
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val service: ApiService = Retrofit.Builder()
        .baseUrl(API_BASE_URL).client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)

    /**
     *  Permet de se connecter et retourne l'utilisateur connecté si il n'y a pas d'erreur
     */
    fun authenticate(loginDto: LoginDto): Single<ResultApi<Token>> = service.authenticate(loginDto)

    /**
     * Route permettant de récupérer les informations de l'utilisateur connecté en fonction de son token
     */
    fun getCurrentUser() : Single<ResultApi<User>> = service.getCurrentUser()
}