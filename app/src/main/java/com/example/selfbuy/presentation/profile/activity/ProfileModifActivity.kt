package com.example.selfbuy.presentation.profile.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            setTitle(R.string.edit_profil)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_modif_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var result = true

        if(item.itemId == R.id.profile_modif_menu_save) {
            //On redirige vers la page de modif du profile en passant l'utilisateur en cours
            Toast.makeText(this, "SAVE", Toast.LENGTH_LONG).show()

            result = super.onOptionsItemSelected(item)
        }
        else if(item.itemId == android.R.id.home){
            super.onBackPressed()
        }

        return result
    }
}
