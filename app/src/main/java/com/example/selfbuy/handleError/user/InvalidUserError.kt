package com.example.selfbuy.handleError.user

import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ErrorApiDto
import com.example.selfbuy.handleError.base.AbstractHandleError
import com.example.selfbuy.presentation.SFApplication

object InvalidUserError : AbstractHandleError(){
    override fun handleError(code: String): ErrorApiDto =
        when(code) {
            "CANNOT_GET_USER" -> ErrorApiDto(code, SFApplication.app.resources.getString(R.string.cannot_get_user))
            "CANNOT_UPDATE_USER" -> ErrorApiDto(code, SFApplication.app.resources.getString(R.string.cannot_update_user))
            "BAD_CREDENTIALS" -> ErrorApiDto(code, SFApplication.app.resources.getString(R.string.bad_credentials))
            "UNKNOWN_HOST" -> ErrorApiDto(code, SFApplication.app.resources.getString(R.string.no_internet_access))
            else -> ErrorApiDto(code, SFApplication.app.resources.getString(R.string.error_call_api))
        }
}