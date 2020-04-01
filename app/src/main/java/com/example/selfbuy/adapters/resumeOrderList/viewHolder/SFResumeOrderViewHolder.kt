package com.example.selfbuy.adapters.resumeOrderList.viewHolder

import android.view.View
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.viewHolders.SFListAdapterViewHolder
import com.example.selfbuy.room.entity.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.resume_order_cell_layout.view.*

class SFResumeOrderViewHolder(itemView: View): SFListAdapterViewHolder<Product, String>(itemView){
    override fun bind(entity: Product, onClick: (String) -> Unit) {
        itemView.tw_product_name_resume_order.text = entity.name

        Picasso.get()
            .load(entity.image)
            .placeholder(R.drawable.no_image_available)
            .into(itemView.img_product_resume_order)

        itemView.tw_product_qty_resume_order.text = entity.quantity.toString()

        val priceTotalWithQty = "%.2f".format(entity.price * entity.quantity)
        val priceProduct = "$priceTotalWithQty${itemView.resources.getString(R.string.euro_symbol)}"
        itemView.tw_product_price_resume_order.text = priceProduct
    }
}