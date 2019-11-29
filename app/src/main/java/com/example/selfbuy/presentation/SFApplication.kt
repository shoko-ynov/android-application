package com.example.selfbuy.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.connexion.ConnexionRepository
import com.example.selfbuy.data.repository.user.UserRepository

class SFApplication: Application() {

    companion object {
        lateinit var app: SFApplication
    }

    lateinit var connexionRepository: ConnexionRepository
    lateinit var userRepository: UserRepository

    lateinit var loginPreferences: SharedPreferences
    lateinit var loginPrefsEditor: SharedPreferences.Editor

    override fun onCreate() {
        super.onCreate()
        app = this

        initInjection()
        initPreferences()
    }

    private fun initInjection() {
        val apiManager = ApiManager()
        connexionRepository = ConnexionRepository(apiManager)
        userRepository = UserRepository(apiManager)
    }

    @SuppressLint("CommitPrefEdits")
    private fun initPreferences(){
        loginPreferences = app.getSharedPreferences("loginPrefs", AppCompatActivity.MODE_PRIVATE)
        loginPrefsEditor = loginPreferences.edit()
    }
}