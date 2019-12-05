package com.example.selfbuy.presentation.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.UserDto
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment(val userDto: UserDto) : Fragment() {

    //private val profileViewModel: ProfileViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tw_user_mail.text = this.userDto.mail
    }
}
