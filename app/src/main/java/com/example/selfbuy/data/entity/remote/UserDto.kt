package com.example.selfbuy.data.entity.remote

data class UserDto(val isAdmin: Boolean,
                   val _id: String,
                   val mail: String,
                   val activationKey: String?,
                   val active: Boolean,
                   val registrationDate: Number,
                   val firstName: String?,
                   val lastName: String?,
                   val address: String?,
                   val city: String?,
                   val postalCode: String?)