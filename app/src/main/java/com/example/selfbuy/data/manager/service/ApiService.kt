package com.example.selfbuy.data.manager.service

import com.example.selfbuy.data.entity.remote.Token
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.data.entity.remote.ResultApi
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     *  Route pour l'authentification
     */
    @POST("auth")
    fun authenticate(@Body loginDto: LoginDto) : Single<ResultApi<Token>>
}