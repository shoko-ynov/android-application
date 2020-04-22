package com.example.selfbuy.presentation.creditCard.activity

import android.os.Bundle
import com.example.selfbuy.R
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.creditCard.fragment.ListCreditsCardFragment

class ListCreditsCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_credits_card)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.list_credit_cards_activity_fragment_container, ListCreditsCardFragment())
                .commit()

            setTitle(R.string.my_cards)
        }
    }
}
