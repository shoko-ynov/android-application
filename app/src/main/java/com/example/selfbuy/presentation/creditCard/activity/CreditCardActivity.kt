package com.example.selfbuy.presentation.creditCard.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.selfbuy.R
import com.example.selfbuy.presentation.creditCard.fragment.CreditCardFragment

class CreditCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.credit_card_activity_fragment_container, CreditCardFragment())
                .commit()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            setTitle(R.string.add_credit_card)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
