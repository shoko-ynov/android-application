package com.example.selfbuy.presentation.order.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.order.fragments.OrderFragment

open class OrderActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val selectedCreditCardId = intent.getStringExtra("selectedCreditCard")
        if (savedInstanceState == null) {

            if(!selectedCreditCardId.isNullOrEmpty()){
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.order_activity_fragment_container, OrderFragment(selectedCreditCardId))
                    .commit()

                setTitle(R.string.resume_order)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.order_activity_fragment_container)

        if (fragment !is OnBackPressedListener || !(fragment as OnBackPressedListener?)!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    interface OnBackPressedListener {
        fun onBackPressed(): Boolean
    }
}
