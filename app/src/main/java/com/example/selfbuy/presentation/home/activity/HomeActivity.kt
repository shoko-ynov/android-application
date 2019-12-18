package com.example.selfbuy.presentation.home.activity

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.home.fragments.CartFragment
import com.example.selfbuy.presentation.home.fragments.ConnexionFragment
import com.example.selfbuy.presentation.home.fragments.HomeFragment
import com.example.selfbuy.presentation.profile.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

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

            this.title = getString(R.string.home)
        }

        setUpBottomNavigationView()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
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

                this.title = getString(R.string.home)
                this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_cart -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_fragment_container, cartFragment)
                    .commit()

                this.title = getString(R.string.cart)
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
                this.title = getString(R.string.profile)
                this.supportActionBar?.setDisplayHomeAsUpEnabled(false)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
