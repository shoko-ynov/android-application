package com.example.selfbuy.presentation.home.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.entity.remote.User
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.home.fragments.CartFragment
import com.example.selfbuy.presentation.home.fragments.ConnexionFragment
import com.example.selfbuy.presentation.home.fragments.HomeFragment
import com.example.selfbuy.presentation.home.viewModels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_connexion.*

class HomeActivity : BaseActivity() {

    private val homeFragment = HomeFragment()
    private val cartFragment = CartFragment()
    private val connexionFragment = ConnexionFragment()

    private val userViewModel = UserViewModel()

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
        bindUserViewModel()
    }

    /**
     * On s'abonne aux differents evenements de UserViewModel
     */
    private fun bindUserViewModel(){
        userViewModel.userLiveData.observe(this, Observer { result: ResultApi<User> ->

            //Passer l'utilisateur a l'activity de profile et charger le fragment profile
            val user = result.data
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_fragment_container, homeFragment)
                .commit()
        })

        userViewModel.errorLiveData.observe(this, Observer { error: Throwable ->
            //si probleme revenir sur le fragment connexion
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_fragment_container, connexionFragment)
                .commit()
        })
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
                userViewModel.getCurrentUser()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
