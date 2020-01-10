package com.example.selfbuy.adapters.productCartList

import android.view.View
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.SFListAdapter
import com.example.selfbuy.adapters.productCartList.viewHolder.SFProductsCartViewHolder
import com.example.selfbuy.room.entity.Product

class SFProductCartListAdapter: SFListAdapter<Product, String, SFProductsCartViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.product_cart_cell_layout
    }

    override fun getViewHolder(view: View): SFProductsCartViewHolder = SFProductsCartViewHolder(view)
}