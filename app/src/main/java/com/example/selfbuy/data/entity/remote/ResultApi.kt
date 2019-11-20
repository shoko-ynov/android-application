package com.example.selfbuy.data.entity.remote

data class ResultApi<TEntity>(val success: Boolean, val data: TEntity?, val error: ErrorApi)