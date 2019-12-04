package com.example.selfbuy.adapters.productList.viewHolder

import android.view.View
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.viewHolders.SFListAdapterViewHolder
import com.example.selfbuy.data.entity.remote.ProductDto
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_cell_layout.view.*

class SFProductsViewHolder(itemView: View): SFListAdapterViewHolder<ProductDto, String>(itemView) {

    override fun bind(entity: ProductDto, onClick: (String) -> Unit) {
        itemView.tw_product_name.text = entity.name
        Picasso.get().load(entity.images[0]).into(itemView.img_product)

        val priceProduct = "${entity.price}${itemView.resources.getString(R.string.euro_symbol)}"
        itemView.tw_product_price.text = priceProduct

        itemView.setOnClickListener { onClick(entity._id) }
    }
}