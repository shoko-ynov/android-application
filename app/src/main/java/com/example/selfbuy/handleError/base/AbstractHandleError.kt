package com.example.selfbuy.handleError.base

import com.example.selfbuy.data.entity.remote.ErrorApiDto

abstract class AbstractHandleError {
    abstract fun handleError(code: String): ErrorApiDto
}