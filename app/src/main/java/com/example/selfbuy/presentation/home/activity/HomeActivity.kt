package com.example.selfbuy.presentation.home.activity

import android.os.Bundle
import android.view.MenuItem
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.home.fragments.CartFragment
import com.example.selfbuy.presentation.home.fragments.ConnexionFragment
import com.example.selfbuy.presentation.home.fragments.HomeFragment
import com.example.selfbuy.presentation.profile.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity() {

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

            //tw_title.text = getString(R.string.home)
            setTitle(R.string.home)
            //sw_product.visibility = SearchView.VISIBLE
        }

        setUpBottomNavigationView()

        //supportActionBar?.hide()
    }

    /**
     * Initialisation des différents onglets
     */
    private fun setUpBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.navbar_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    /**
     * Evenement appelé lorsque sélectionne une option native à la plateforme (comme le bouton retour dans l'actionbar)
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null && item.itemId == android.R.id.home) {
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
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, homeFragment)
                    .commit()

                //tw_title.text = getString(R.string.home)
                setTitle(R.string.home)
                //sw_product.visibility = SearchView.VISIBLE
                this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_cart -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, cartFragment)
                    .commit()

                //tw_title.text = getString(R.string.cart)
                setTitle(R.string.cart)
                //sw_product.visibility = SearchView.GONE
                this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {

                if (CurrentUser.userDto != null){
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.home_activity_fragment_container, ProfileFragment(CurrentUser.userDto!!))
                        .commit()
                }
                else{
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.home_activity_fragment_container, connexionFragment)
                        .commit()
                }
                //sw_product.visibility = SearchView.GONE
                //tw_title.text = getString(R.string.profile)
                setTitle(R.string.profile)
                this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
