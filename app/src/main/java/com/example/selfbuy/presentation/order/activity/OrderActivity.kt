package com.example.selfbuy.presentation.order.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.order.fragments.OrderFragment

open class OrderActivity : BaseActivity() {

    protected var onBackPress: OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.order_activity_fragment_container, OrderFragment())
                .commit()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            setTitle(R.string.resume_order)
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
