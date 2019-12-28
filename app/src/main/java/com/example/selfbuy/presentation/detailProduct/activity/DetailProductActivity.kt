package com.example.selfbuy.presentation.detailProduct.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.selfbuy.R
import com.example.selfbuy.presentation.detailProduct.fragment.DetailProductFragment

class DetailProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val idProduct = intent.getStringExtra("idProduct")

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.product_activity_fragment_container, DetailProductFragment(idProduct))
                .commit()
        }

        this.title = getString(R.string.detail)
    }
}
