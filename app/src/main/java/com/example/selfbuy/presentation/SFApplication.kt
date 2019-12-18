package com.example.selfbuy.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.connexion.ConnexionRepository
import com.example.selfbuy.data.repository.product.ProductRepository
import com.example.selfbuy.data.repository.user.UserRepository
import com.example.selfbuy.room.database.AppDatabase

class SFApplication: Application() {

    companion object {
        lateinit var app: SFApplication
    }

    lateinit var connexionRepository: ConnexionRepository
    lateinit var userRepository: UserRepository
    lateinit var productRepository: ProductRepository

    lateinit var loginPreferences: SharedPreferences
    lateinit var loginPrefsEditor: SharedPreferences.Editor

    lateinit var dbRoom: AppDatabase

    override fun onCreate() {
        super.onCreate()
        app = this

        initInjection()
        initPreferences()
        initDatabaseRoom()
    }

    private fun initInjection() {
        val apiManager = ApiManager()
        connexionRepository = ConnexionRepository(apiManager)
        userRepository = UserRepository(apiManager)
        productRepository = ProductRepository(apiManager)
    }

    @SuppressLint("CommitPrefEdits")
    private fun initPreferences(){
        loginPreferences = app.getSharedPreferences("loginPrefs", AppCompatActivity.MODE_PRIVATE)
        loginPrefsEditor = loginPreferences.edit()
    }

    private fun initDatabaseRoom(){
        dbRoom = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "product-cart"
        ).build()
    }
}