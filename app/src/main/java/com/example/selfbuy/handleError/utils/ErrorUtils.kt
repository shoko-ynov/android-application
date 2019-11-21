package com.example.selfbuy.handleError.utils

import android.view.View
import com.example.selfbuy.data.entity.remote.ErrorApi
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.handleError.user.InvalidUserError
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object ErrorUtils {

    fun getErrorApi(errorThrowable: Throwable): ErrorApi{
        var errorBody = ""

        try {
            val error = errorThrowable as HttpException
            val reader: BufferedReader?
            reader = BufferedReader(InputStreamReader(error.response().errorBody()?.byteStream()))

            try {
                errorBody = reader.readLine()
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        val resultApiBody = Gson().fromJson(errorBody, ResultApi::class.java)
        return InvalidUserError.handleError(resultApiBody.error.code)
    }
}