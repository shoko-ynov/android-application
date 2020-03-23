package com.example.selfbuy.presentation.profile.activity

import android.os.Bundle
import com.example.selfbuy.R
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.profile.fragments.ProfileModifFragment

class ProfileModifActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_modif)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.profile_modif_activity_fragment_container, ProfileModifFragment())
                .commit()

            actionBar?.setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.edit_profil)
        }
    }
}
