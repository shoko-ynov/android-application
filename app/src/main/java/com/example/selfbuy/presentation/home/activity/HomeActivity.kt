package com.example.selfbuy.presentation.home.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.home.fragments.CartFragment
import com.example.selfbuy.presentation.home.fragments.ConnexionFragment
import com.example.selfbuy.presentation.home.fragments.HomeFragment
import com.example.selfbuy.presentation.profile.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_cart.*

class HomeActivity : BaseActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            val showCart = intent.getBooleanExtra("showCart", false)
            if(showCart){
                val navView:BottomNavigationView = findViewById(R.id.navbar_view)
                navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
                navView.selectedItemId = R.id.navigation_cart
            }
            else{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, HomeFragment())
                    .commit()

                setTitle(R.string.home)
                //sw_product.visibility = SearchView.VISIBLE
            }
        }

        setUpBottomNavigationView()

        supportActionBar?.hide()
    }

    /**
     * Initialisation des différents onglets
     */
    private fun setUpBottomNavigationView() {
        val navView:BottomNavigationView = findViewById(R.id.navbar_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    /**
     * Evenement appelé lorsque sélectionne une option native à la plateforme (comme le bouton retour dans l'actionbar)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Evenement appelé lorsqu'on change d'onglet
     */
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                this.supportActionBar?.hide()

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, HomeFragment())
                    .commit()

                setTitle(R.string.home)
                //sw_product.visibility = SearchView.VISIBLE
                this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_cart -> {
                this.showAndSelectCartFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                if (CurrentUser.userDto != null){
                    this.supportActionBar?.show()

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.home_activity_fragment_container, ProfileFragment(CurrentUser.userDto!!))
                        .commit()
                }
                else{
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.home_activity_fragment_container, ConnexionFragment())
                        .commit()
                }

                setTitle(R.string.profile)
                this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showAndSelectCartFragment(){
        supportActionBar?.hide()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.home_activity_fragment_container, CartFragment())
            .commit()

        setTitle(R.string.cart)
        //sw_product.visibility = SearchView.GONE
        this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}
