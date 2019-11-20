package com.example.selfbuy.presentation

import android.app.Application
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.connexion.ConnexionRepository

class SFApplication: Application() {

    companion object {
        lateinit var app: SFApplication
    }

    lateinit var connexionRepository: ConnexionRepository

    override fun onCreate() {
        super.onCreate()
        app = this

        initInjection()
    }

    private fun initInjection() {
        val apiManager = ApiManager()
        connexionRepository = ConnexionRepository(apiManager)
    }
}