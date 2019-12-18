package com.example.selfbuy.presentation.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.google.android.material.snackbar.Snackbar

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_cart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mockProductCart()
    }

    private fun mockProductCart(){
        val product = Product("uid", "image", "name", 2.5, "description", "category", 2)

        Async {
            val resultQuery = getAllProduct()
            if(resultQuery.count() > 0 )
                view?.let { v -> Snackbar.make(v, resultQuery[0].name, Snackbar.LENGTH_LONG).show() }
            else
                view?.let { v -> Snackbar.make(v, "Empty", Snackbar.LENGTH_LONG).show() }
        }.execute()
    }

    private fun getAllProduct(): List<Product>{
        return SFApplication.app.dbRoom.productDao().getAll()
    }
}
