package com.example.selfbuy.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.connexion.ConnexionRepository
import com.example.selfbuy.data.repository.connexion.InscriptionRepository
import com.example.selfbuy.data.repository.payment.PaymentRepository
import com.example.selfbuy.data.repository.product.ProductRepository
import com.example.selfbuy.data.repository.user.UserRepository
import com.example.selfbuy.room.database.AppDatabase
import com.stripe.android.PaymentConfiguration

class SFApplication: Application() {

    companion object {
        lateinit var app: SFApplication
    }

    lateinit var connexionRepository: ConnexionRepository
    lateinit var inscriptionRepository: InscriptionRepository
    lateinit var userRepository: UserRepository
    lateinit var productRepository: ProductRepository
    lateinit var paymentRepository: PaymentRepository

    lateinit var loginPreferences: SharedPreferences
    lateinit var loginPrefsEditor: SharedPreferences.Editor

    lateinit var dbRoom: AppDatabase

    override fun onCreate() {
        super.onCreate()
        app = this

        initInjection()
        initPreferences()
        initDatabaseRoom()
        initStripe()
    }

    private fun initStripe(){
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_QaiIO5kPkgG7O1mVrUkBtxuT00e0pQ3xq2"
        )
    }

    private fun initInjection() {
        val apiManager = ApiManager()
        connexionRepository = ConnexionRepository(apiManager)
        inscriptionRepository = InscriptionRepository(apiManager)
        userRepository = UserRepository(apiManager)
        productRepository = ProductRepository(apiManager)
        paymentRepository = PaymentRepository(apiManager)
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