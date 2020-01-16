package com.example.selfbuy.adapters.productCartList.viewHolder

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.viewHolders.SFListAdapterViewHolder
import com.example.selfbuy.room.entity.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.product_cart_cell_layout.view.*

class SFProductsCartViewHolder(itemView: View): SFListAdapterViewHolder<Product, String>(itemView) {

    private val quantities = itemView.resources.getStringArray(R.array.quantities_array)
    private var isFirstLoad = true

    override fun bind(entity: Product, onClick: (String) -> Unit) {
        itemView.tw_product_name_cart.text = entity.name
        Picasso.get().load(entity.image).into(itemView.img_product_cart)

        this.initSpinnerAndSetOnClickListener(entity)
        this.setQuantity(entity)

        itemView.btn_remove_from_cart.setOnClickListener { onClick(entity.uid) }
    }

    private fun setQuantity(product: Product){
        val priceTotalWithQty = product.price * product.quantity
        val priceProduct = "$priceTotalWithQty${itemView.resources.getString(R.string.euro_symbol)}"
        itemView.tw_product_price_cart.text = priceProduct
        itemView.tw_product_qty_cart.text = product.quantity.toString()
    }

    private fun initSpinnerAndSetOnClickListener(product: Product){
        if (this.itemView.context != null){
            val dataAdapter: ArrayAdapter<String> = ArrayAdapter(this.itemView.context, R.layout.support_simple_spinner_dropdown_item, quantities)
            dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            this.itemView.spinner_quantity_cart.adapter = dataAdapter
        }

        this.itemView.spinner_quantity_cart.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                if (isFirstLoad){
                    val index = quantities.indexOf(product.quantity.toString())
                    itemView.spinner_quantity_cart.setSelection(index)
                    isFirstLoad = false
                }
                else{
                    product.quantity = quantities[position].toInt()
                    setQuantity(product)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                setQuantity(product)
            }
        }
    }
}