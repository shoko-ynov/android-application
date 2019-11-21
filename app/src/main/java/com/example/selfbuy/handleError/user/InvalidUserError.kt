package com.example.selfbuy.handleError.user

import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ErrorApi
import com.example.selfbuy.handleError.base.AbstractHandleError
import com.example.selfbuy.presentation.SFApplication

object InvalidUserError : AbstractHandleError(){
    override fun handleError(code: String): ErrorApi =
        when(code) {
            "CANNOT_GET_USER" -> ErrorApi(code, SFApplication.app.resources.getString(R.string.cannot_get_user))
            "CANNOT_UPDATE_USER" -> ErrorApi(code, SFApplication.app.resources.getString(R.string.cannot_update_user))
            "BAD_CREDENTIALS" -> ErrorApi(code, SFApplication.app.resources.getString(R.string.bad_credentials))
            else -> ErrorApi(code, SFApplication.app.resources.getString(R.string.error_call_api))
        }
}