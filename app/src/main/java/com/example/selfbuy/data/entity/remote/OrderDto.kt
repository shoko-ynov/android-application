package com.example.selfbuy.data.entity.remote

data class OrderDto(
    val products : ArrayList<DetailOrderDto>,
    val cardId: String
)

data class DetailOrderDto(
    val productId : String,
    val quantity : Int
)