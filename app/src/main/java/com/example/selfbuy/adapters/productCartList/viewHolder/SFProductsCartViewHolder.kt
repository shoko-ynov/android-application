package com.example.selfbuy.adapters.productCartList.viewHolder

import android.view.View
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.viewHolders.SFListAdapterViewHolder
import com.example.selfbuy.room.entity.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_cart_cell_layout.view.*

class SFProductsCartViewHolder(itemView: View): SFListAdapterViewHolder<Product, String>(itemView) {

    override fun bind(entity: Product, onClick: (String) -> Unit) {
        itemView.tw_product_name_cart.text = entity.name
        Picasso.get().load(entity.image).into(itemView.img_product_cart)

        val priceProduct = "${entity.price}${itemView.resources.getString(R.string.euro_symbol)}"
        itemView.tw_product_price_cart.text = priceProduct

        itemView.tw_product_qty_cart.text = entity.quantity.toString()

        //itemView.setOnClickListener { onClick(entity.uid) }

        itemView.btn_remove_from_cart.setOnClickListener { onClick(entity.uid) }
    }
}