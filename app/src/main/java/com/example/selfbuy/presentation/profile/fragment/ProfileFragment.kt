package com.example.selfbuy.presentation.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.UserDto
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment(private val userDto: UserDto) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tw_user_mail.text = this.userDto.mail
        this.setOnClickListener()
    }

    /**
     * On s'abonne aux evenements onclick des differents boutons de la vue
     */
    private fun setOnClickListener(){
        btn_user_account.setOnClickListener{
           Toast.makeText(this.activity, getString(R.string.account), Toast.LENGTH_LONG).show()
        }

        btn_user_orders.setOnClickListener{
            Toast.makeText(this.activity, getString(R.string.orders), Toast.LENGTH_LONG).show()
        }

        btn_user_payment.setOnClickListener{
            Toast.makeText(this.activity, getString(R.string.payment), Toast.LENGTH_LONG).show()
        }

        btn_user_privacy.setOnClickListener{
            Toast.makeText(this.activity, getString(R.string.privacy), Toast.LENGTH_LONG).show()
        }

        btn_user_aboutus.setOnClickListener{
            Toast.makeText(this.activity, getString(R.string.aboutus), Toast.LENGTH_LONG).show()
        }
    }
}
