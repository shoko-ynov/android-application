package com.example.selfbuy.presentation.order.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.order.fragments.SelectCreditCardFragment

class SelectCreditCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_credit_card)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.select_credit_card_activity_fragment_container, SelectCreditCardFragment())
                .commit()

            setTitle(R.string.payement_method)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.select_credit_card_activity_fragment_container)

        if (fragment !is OnBackPressedListener || !(fragment as OnBackPressedListener?)!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    interface OnBackPressedListener {
        fun onBackPressed(): Boolean
    }
}
