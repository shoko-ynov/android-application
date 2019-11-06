package com.example.selfbuy.presentation.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.selfbuy.R
import com.example.selfbuy.presentation.main.fragments.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_main_fragment_container, MainFragment())
                .commit()
        }
    }
}
