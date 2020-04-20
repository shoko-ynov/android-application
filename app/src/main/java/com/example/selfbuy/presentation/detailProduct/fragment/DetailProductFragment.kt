package com.example.selfbuy.presentation.detailProduct.fragment

import android.content.Intent
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
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.detailProduct.viewModel.ProductViewModel
import com.example.selfbuy.presentation.home.activity.HomeActivity
import com.example.selfbuy.presentation.home.fragments.CartFragment
import com.example.selfbuy.presentation.home.fragments.HomeFragment
import com.example.selfbuy.presentation.order.activity.OrderActivity
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageThread
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail_product.*

class DetailProductFragment(private val idProduct : String) : Fragment() {

    private var productInCart: Product? = null
    private val productViewModel = ProductViewModel()
    private lateinit var product: ProductDto
    private lateinit var quantities: Array<String>
    private var selectedQuantity = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_detail_product, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quantities = resources.getStringArray(R.array.quantities_array)
        selectedQuantity = quantities[0].toInt()

        if (this.context != null){
            val dataAdapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.spinner_item, quantities)
            dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            spinner_quantity.adapter = dataAdapter
        }

        this.bindProductViewModel()
        this.setBtnAddCartOnClickListener()
        this.setBtnSeeCartOnClickListener()

        progressBar_detail_product.visibility = View.VISIBLE
        productViewModel.getProductById(idProduct)
    }

    /**
     * Appelé lorsque le bouton ajouter au panier est cliqué
     */
    private fun setBtnAddCartOnClickListener(){
        btn_detail_product_add_cart.setOnClickListener {
            val productToAddToCart =
                Product(this.product._id, this.product.images[0], this.product.name, this.product.price,
                    this.product.description, this.product.category, selectedQuantity)

            Async {
                try {
                    if(productInCart == null){
                        productInCart = productToAddToCart
                        SFApplication.app.dbRoom.productDao().insertAll(productToAddToCart)

                        ManageThread.executeOnMainThread(this.context!!) {
                            selectQuantity()
                        }

                        view?.let { v -> Snackbar.make(v, getString(R.string.product_added_to_cart), Snackbar.LENGTH_LONG).show() }
                    }
                    else{
                        productToAddToCart.quantity = selectedQuantity
                        SFApplication.app.dbRoom.productDao().update(productToAddToCart)

                        view?.let { v -> Snackbar.make(v, getString(R.string.quantity_modified_to_cart), Snackbar.LENGTH_LONG).show() }
                    }
                }
                catch (e: Exception){
                    view?.let { v -> Snackbar.make(v, getString(R.string.error_add_cart), Snackbar.LENGTH_LONG).show() }
                }
            }.execute()
        }
    }

    /**
     * Appelé lorsque le bouton voir mon panier est cliqué
     */
    private fun setBtnSeeCartOnClickListener(){
        btn_detail_product_see_cart.setOnClickListener {
            val intent = Intent(this.context, HomeActivity::class.java)
            intent.putExtra("showCart", true)
            startActivity(intent)

            this.activity?.finish()
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
                selectedQuantity = quantities[position].toInt()
                val newPrice = product.price * quantities[position].toInt()
                val priceFormated = "%.2f".format(newPrice)
                tw_detail_product_price.text = priceFormated
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                val priceFormated = "${product.price}"
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
                Picasso.get()
                    .load(product.images[0])
                    .placeholder(R.drawable.no_image_available)
                    .into(tw_detail_product_image)
                tw_detail_product_name.text = product.name

                if(product.description.isNotEmpty()){
                    tw_detail_product_description.text = product.description
                }
                else{
                    tw_detail_product_description.text = getString(R.string.unspecified)
                }

                tw_detail_product_price.text = product.price.toString()

                this.product = product
                this.checkProductExistInCart()
                this.spinnerItemOnClickListener()
            }
        })

        productViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_detail_product.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    /**
     * Verifie que le produit existe dans le panier
     */
    private fun checkProductExistInCart(){
        Async {
            try {
                productInCart = SFApplication.app.dbRoom.productDao().getById(this.product._id)

                ManageThread.executeOnMainThread(this.context!!) {
                    selectQuantity()
                }
            }
            catch (e: Exception){
                view?.let { v -> Snackbar.make(v, getString(R.string.error_load_product), Snackbar.LENGTH_LONG).show() }
            }
        }.execute()
    }

    /**
     * Selectionne la quantité
     */
    private fun selectQuantity(){
        if(productInCart != null){
            btn_detail_product_add_cart.text = getString(R.string.modify_quantity_product)
            val index = quantities.indexOf(productInCart!!.quantity.toString())
            selectedQuantity = quantities[index].toInt()
            spinner_quantity.setSelection(index)
        }
    }
}
