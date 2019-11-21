package com.example.selfbuy.handleError.base

import com.example.selfbuy.data.entity.remote.ErrorApi

abstract class AbstractHandleError {
    abstract fun handleError(code: String): ErrorApi
}