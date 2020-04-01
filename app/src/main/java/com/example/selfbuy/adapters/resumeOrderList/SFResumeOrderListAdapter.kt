package com.example.selfbuy.adapters.resumeOrderList

import android.view.View
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.SFListAdapter
import com.example.selfbuy.adapters.resumeOrderList.viewHolder.SFResumeOrderViewHolder
import com.example.selfbuy.room.entity.Product

class SFResumeOrderListAdapter : SFListAdapter<Product, String, SFResumeOrderViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.resume_order_cell_layout
    }

    override fun getViewHolder(view: View): SFResumeOrderViewHolder = SFResumeOrderViewHolder(view)
}