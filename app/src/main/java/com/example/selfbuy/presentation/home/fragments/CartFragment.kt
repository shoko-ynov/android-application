package com.example.selfbuy.presentation.home.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.productCartList.SFProductCartListAdapter
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_home.*

class CartFragment : Fragment() {

    private val productListAdapter = SFProductCartListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_cart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products_cart_recycle_view.apply { getCartProduct(this) }
    }

    /**
     * Recuperer les produits dans la base room pour pouvoir les afficher dans le panierx
     */
    private fun getCartProduct(recycleView: RecyclerView) {
        products_cart_recycle_view.layoutManager = LinearLayoutManager(this.context)

        Async {
            val resultQuery = SFApplication.app.dbRoom.productDao().getAll()

            // Get a handler that can be used to post to the main thread
            val mainHandler = Handler(this.context?.mainLooper)
            val myRunnable = Runnable {
                loadDataInRecycleView(resultQuery.toMutableList(), recycleView)
            }
            mainHandler.post(myRunnable)
        }.execute()
    }

    /**
     * Charge les données dans la recycleview
     */
    private fun loadDataInRecycleView(product: MutableList<Product>, recycleView: RecyclerView){
        this.updateTextViewEmptyCartListProduct(product)

        productListAdapter.onClickListener = { id ->
            Async {
                val productToDelete = SFApplication.app.dbRoom.productDao().getById(id)
                if (productToDelete != null) {
                    SFApplication.app.dbRoom.productDao().delete(productToDelete)
                    product.remove(productToDelete)
                    // Get a handler that can be used to post to the main thread
                    val mainHandler = Handler(this.context?.mainLooper)
                    val myRunnable = Runnable {
                        this.updateTextViewEmptyCartListProduct(product)
                    }
                    mainHandler.post(myRunnable)
                }
            }.execute()
        }

        recycleView.adapter = productListAdapter
    }

    /**
     * Met à jour le textview indiquant que la liste est vide
     */
    private fun updateTextViewEmptyCartListProduct(productList: List<Product>){
        productListAdapter.updateList(productList)

        if(productList.any()){
            twEmptyCartListProduct.visibility = TextView.INVISIBLE
        } else {
            twEmptyCartListProduct.visibility = TextView.VISIBLE
        }
    }
}
