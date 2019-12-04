package com.example.selfbuy.presentation.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.productList.SFProductListAdapter
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.home.viewModels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private val homeViewModel = HomeViewModel()
    private val productListAdapter = SFProductListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products_recycle_view.apply { bindHomeViewModel(this) }
        this.setSwipeRefreshListener()
        this.loadProducts()
    }

    /**
     * On s'abonne aux differents evenements de HomeViewModel
     */
    private fun bindHomeViewModel(recycleView: RecyclerView){
        homeViewModel.productLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<ArrayList<ProductDto>> ->
            progressBar_list_product.visibility = View.GONE

            products_recycle_view.layoutManager = GridLayoutManager(this.context, 2)

            if (resultDto.data != null){
                updateList(resultDto.data)
                productListAdapter.onClickListener = { id ->
                    view?.let { v -> Snackbar.make(v, id, Snackbar.LENGTH_LONG).show() }
                }

                recycleView.adapter = productListAdapter
            }
        })

        homeViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_list_product.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    /**
     * Met a jour la liste des produits dans l'adapter
     */
    private fun updateList(productList: List<ProductDto>) {
        product_refresh_layout.isRefreshing = false
        productListAdapter.updateList(productList)

        if(productList.any()){
            twEmptyListProduct.visibility = TextView.INVISIBLE
        }
        else{
            twEmptyListProduct.visibility = TextView.VISIBLE
        }
    }

    /**
     * Charge la liste de produits
     */
    private fun loadProducts(){
        progressBar_list_product.visibility = View.VISIBLE
        homeViewModel.getProducts()
    }

    /**
     * Gere le swipe refresh de la liste des produits
     */
    private fun setSwipeRefreshListener() {
        product_refresh_layout.setOnRefreshListener {
            this.loadProducts()
        }
    }
}
