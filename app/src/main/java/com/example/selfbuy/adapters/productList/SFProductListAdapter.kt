package com.example.selfbuy.adapters.productList

import android.view.View
import com.example.selfbuy.adapters.productList.viewHolder.SFProductsViewHolder
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.SFListAdapter
import com.example.selfbuy.data.entity.remote.ProductDto

class SFProductListAdapter: SFListAdapter<ProductDto, String, SFProductsViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.product_cell_layout
    }

    override fun getViewHolder(view: View): SFProductsViewHolder = SFProductsViewHolder(view)
}