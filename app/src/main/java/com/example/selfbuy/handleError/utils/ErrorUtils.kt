package com.example.selfbuy.handleError.utils

import com.example.selfbuy.data.entity.remote.ErrorApi
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.handleError.user.InvalidUserError
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.UnknownHostException

object ErrorUtils {

    fun getErrorApi(errorThrowable: Throwable): ErrorApi{
        var codeResult = errorThrowable.message.toString()

        try {
            val error = errorThrowable as HttpException
            val reader = BufferedReader(InputStreamReader(error.response().errorBody()?.byteStream()))

            val errorBody = reader.readLine()

            codeResult = if (errorBody == "Unauthorized"){
                errorThrowable.code().toString()
            } else{
                val resultApiBody = Gson().fromJson(errorBody, ResultApi::class.java)
                resultApiBody.error.code
            }
        }
        catch (e: Exception) {
            if (errorThrowable is UnknownHostException){
                codeResult = "UNKNOWN_HOST"
            }
            return InvalidUserError.handleError(codeResult)
        }
        return InvalidUserError.handleError(codeResult)
    }
}