package com.example.selfbuy.data.entity.remote

data class CreditCardDto(
    val isDefaultCard: Boolean,
    val _id: String?,
    val userId: String?,
    val expMonth: Int?,
    val expYear: Int?,
    val last4: String?,
    val name: String?,
    val brand: String?
)
