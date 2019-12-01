package com.example.selfbuy.data.manager.service

import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.data.manager.api.BaseApi
import com.example.selfbuy.presentation.SFApplication
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class InterceptorService : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response? {
        val originalRequest = chain.request()
        var request = originalRequest

        synchronized(this){
            if(request.header("No-Authentication")==null){
                if(!CurrentUser.tokenDto?.token.isNullOrEmpty())
                {
                    val finalToken =  "Bearer ${CurrentUser.tokenDto!!.token}"
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
                                    .refreshToken(CurrentUser.tokenDto!!.refreshToken)
                                    .execute()

                            return when {
                                responseNewTokenLoginModel.code() != 200 -> {
                                    SFApplication.app.loginPrefsEditor.clear()
                                    SFApplication.app.loginPrefsEditor.commit()

                                    return chain.proceed(request)
                                }
                                else -> {
                                    responseNewTokenLoginModel.body()?.data?.let {
                                        SFApplication.app.loginPrefsEditor.putString("token", it.token)
                                        SFApplication.app.loginPrefsEditor.putString("refreshToken", it.refreshToken)
                                        SFApplication.app.loginPrefsEditor.commit()
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