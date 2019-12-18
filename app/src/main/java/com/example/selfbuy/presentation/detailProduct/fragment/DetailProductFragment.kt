package com.example.selfbuy.presentation.detailProduct.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.detailProduct.viewModel.ProductViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_detail_product.*

class DetailProductFragment(val idProduct : String) : Fragment() {

    private val productViewModel = ProductViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_detail_product, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.bindProductViewModel()

        progressBar_detail_product.visibility = View.VISIBLE
        productViewModel.getProductById(idProduct)
    }

    /**
     * On s'abonne aux differents evenements de ProductViewModel
     */
    private fun bindProductViewModel(){
        productViewModel.productLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<ProductDto> ->
            progressBar_detail_product.visibility = View.GONE
            val product = resultDto.data

            view?.let { v -> Snackbar.make(v, product?.name.toString(), Snackbar.LENGTH_LONG).show() }
        })

        productViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_detail_product.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

}
