package com.example.selfbuy.data.manager.api

import android.content.SharedPreferences
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.data.manager.service.ApiService
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceInterceptor : Interceptor {

    private lateinit var loginPrefsEditor: SharedPreferences.Editor

    override fun intercept(chain: Interceptor.Chain): Response? {
        val originalRequest = chain.request()
        var request = originalRequest

        synchronized(this){
            if(request.header("No-Authentication")==null){
                if(!CurrentUser.token?.token.isNullOrEmpty())
                {
                    val finalToken =  "Bearer ${CurrentUser.token!!.token}"
                    request = request.newBuilder()
                        .addHeader("Authorization", finalToken)
                        .build()

                    val initialResponse = chain.proceed(request)

                    when {
                        initialResponse.code() == 403 || initialResponse.code() == 401 -> {
                            val responseNewTokenLoginModel=
                                Retrofit
                                    .Builder()
                                    .baseUrl(BaseApi.API_BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .build()
                                    .create(ApiService::class.java)
                                    .refreshToken(CurrentUser.token!!.refreshToken)
                                    .execute()

                            return when {
                                responseNewTokenLoginModel.code() != 200 -> {
                                    return chain.proceed(request)
                                }
                                else -> {
                                    responseNewTokenLoginModel.body()?.data?.let {
                                        loginPrefsEditor.putString("token", it.token)
                                        loginPrefsEditor.putString("refreshToken", it.refreshToken)
                                        loginPrefsEditor.commit()
                                    }
                                    val newAuthenticationRequest =
                                        chain
                                            .request()
                                            .newBuilder()
                                            .addHeader("Authorization", "Bearer " + responseNewTokenLoginModel.body()?.data?.token)
                                            .build()

                                    chain.proceed(newAuthenticationRequest)
                                }
                            }
                        }
                        else -> return initialResponse
                    }
                }
            }
        }
        return chain.proceed(request)
    }
}