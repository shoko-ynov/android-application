package com.example.selfbuy.presentation.home.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.selfbuy.R
import com.example.selfbuy.presentation.home.fragments.CartFragment
import com.example.selfbuy.presentation.home.fragments.HomeFragment
import com.example.selfbuy.presentation.home.fragments.ConnexionFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val cartFragment = CartFragment()
    private val connexionFragment = ConnexionFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_fragment_container, HomeFragment())
                .commit()
        }

        setUpBottomNavigationView()
    }

    /**
     * Initialisation des onglets avec l'onglet bus et trams
     */
    private fun setUpBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.navbar_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    /**
     * Evenement appelé lorsqu'on change d'onglet
     */
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, homeFragment)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_cart -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, cartFragment)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, connexionFragment)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
