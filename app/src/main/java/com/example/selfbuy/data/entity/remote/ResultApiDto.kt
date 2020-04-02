package com.example.selfbuy.data.entity.remote

data class ResultApiDto<TEntity>(val success: Boolean?, val data: TEntity?, val error: ErrorApiDto?)