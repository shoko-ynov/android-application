package com.example.selfbuy.data.entity.remote

data class ProductDto(val images: ArrayList<String>,
                      val _id: String,
                      val name: String,
                      val price: Double,
                      val description: String,
                      val category: String)