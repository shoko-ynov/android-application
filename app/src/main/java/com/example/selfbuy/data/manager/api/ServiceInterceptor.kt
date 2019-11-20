package com.example.selfbuy.data.manager.api

import com.example.selfbuy.data.entity.remote.Token
import okhttp3.Interceptor
import okhttp3.Response

class ServiceInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if(request.header("No-Authentication")==null){
            //val token = getTokenFromSharedPreference();
            //or use Token Function
            //if(!Token.token.isNullOrEmpty())
            //{
                //val finalToken =  "Bearer ${Token.token}"
                //request = request.newBuilder()
                    //.addHeader("Authorization", finalToken)
                    //.build()
            //}
        }

        return chain.proceed(request)
    }
}