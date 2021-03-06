package com.example.selfbuy.presentation.order.activity

import android.os.Bundle
import android.view.MenuItem
import com.example.selfbuy.R
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.home.fragments.ConnexionFragment
import com.example.selfbuy.presentation.order.fragments.SelectCreditCardFragment

class SelectCreditCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_credit_card)

        val isConnected = intent.getBooleanExtra("IsConnected", false)

        if (savedInstanceState == null) {
            if(isConnected){
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.select_credit_card_activity_fragment_container, SelectCreditCardFragment())
                    .commit()

                setTitle(R.string.payement_method)
            }
            else{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.select_credit_card_activity_fragment_container, ConnexionFragment())
                    .commit()

                setTitle(R.string.profile)
            }

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
