package com.example.selfbuy.presentation.detailProduct.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.detailProduct.viewModel.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_product.*

class DetailProductFragment(private val idProduct : String) : Fragment() {

    private val productViewModel = ProductViewModel()
    private val quantities = arrayOf("1", "2", "3", "4", "5")
    private var basePriceProduct: Double = 0.0
    private var selectedQuantity = quantities[0]

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_detail_product, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (this.context != null){
            val dataAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, quantities)
            dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            spinner_quantity.adapter = dataAdapter
        }

        this.bindProductViewModel()
        this.setBtnAddCartOnClickListener()

        progressBar_detail_product.visibility = View.VISIBLE
        productViewModel.getProductById(idProduct)
    }

    /**
     * Appelé lorsque le bouton ajouter au panier est cliqué
     */
    private fun setBtnAddCartOnClickListener(){
        btn_detail_product_add_cart.setOnClickListener {
            view?.let { v -> Snackbar.make(v, selectedQuantity.toString(), Snackbar.LENGTH_LONG).show() }
        }
    }

    /**
     * Appelé lorsque la valeur selectionne dans le spinner est modifié
     */
    private fun spinnerItemOnClickListener(){
        spinner_quantity.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                selectedQuantity = quantities[position]
                val newPrice = basePriceProduct * quantities[position].toInt()
                val priceFormated = "$newPrice ${getString(R.string.euro_symbol)}"
                tw_detail_product_price.text = priceFormated
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) { // your code here
                val priceFormated = "$basePriceProduct ${getString(R.string.euro_symbol)}"
                tw_detail_product_price.text = priceFormated
            }
        }
    }

    /**
     * On s'abonne aux differents evenements de ProductViewModel
     */
    private fun bindProductViewModel(){
        productViewModel.productLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<ProductDto> ->
            progressBar_detail_product.visibility = View.GONE
            val product = resultDto.data

            if(product != null){
                Picasso.get().load(product.images[0]).into(tw_detail_product_image)
                tw_detail_product_name.text = product.name
                tw_detail_product_description.text = product.description

                val priceFormated = "${product.price} ${getString(R.string.euro_symbol)}"
                tw_detail_product_price.text = priceFormated

                this.basePriceProduct = product.price
                this.spinnerItemOnClickListener()
            }
        })

        productViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_detail_product.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

}
