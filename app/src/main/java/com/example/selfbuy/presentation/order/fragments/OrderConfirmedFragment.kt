package com.example.selfbuy.presentation.order.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.home.activity.HomeActivity
import com.example.selfbuy.presentation.order.activity.OrderActivity
import com.example.selfbuy.presentation.order.activity.SelectCreditCardActivity
import com.example.selfbuy.room.Async
import com.example.selfbuy.utils.ManageStepOrder
import kotlinx.android.synthetic.main.fragment_order_confirmed.*

class OrderConfirmedFragment : Fragment(), OrderActivity.OnBackPressedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_order_confirmed, container, false)

    override fun onBackPressed(): Boolean {
        val intent = Intent(this.context!!, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val currentActivity = this.activity as AppCompatActivity
        currentActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        currentActivity.setTitle(R.string.command_ended)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ManageStepOrder.nextStep(this.activity as AppCompatActivity)

        this.deleteCartUser()

        btn_order_confirmed_return_home.setOnClickListener {
            this.activity?.onBackPressed()
        }
    }

    /**
     * Supprime tous les produits dans le panier de l'utilisateur
     */
    private fun deleteCartUser(){
        Async {
            SFApplication.app.dbRoom.productDao().deleteAll()
        }.execute()
    }
}