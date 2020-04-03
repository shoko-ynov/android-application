package com.example.selfbuy.presentation.order.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.selfbuy.R
import kotlinx.android.synthetic.main.fragment_select_credit_card.*

class SelectCreditCardFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =inflater.inflate(R.layout.fragment_select_credit_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_confirm_command.setOnClickListener {
            val orderConfirmed = OrderConfirmedFragment()
            val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction().apply {
                replace(R.id.select_credit_card_activity_fragment_container, orderConfirmed)
                addToBackStack(null)
            }
            fragmentTransaction.commit()
        }
    }
}
