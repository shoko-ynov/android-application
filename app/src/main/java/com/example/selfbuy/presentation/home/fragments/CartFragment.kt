package com.example.selfbuy.presentation.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.productCartList.SFProductCartListAdapter
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.order.activity.OrderActivity
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageThread
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment() {
    private val productListAdapter = SFProductCartListAdapter()
    private lateinit var recycleView: RecyclerView
    private lateinit var menu:Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.validate_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        this.menu = menu
        this.loadProductsAndUpdateMenu(menu, recycleView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.check_menu_validate && productListAdapter.itemCount > 0) {
            val intent = Intent(this.context, OrderActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_cart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products_cart_recycle_view.apply { getCartProduct(this) }
    }

    /**
     * Recuperer les produits dans la base room pour pouvoir les afficher dans le panier
     */
    private fun getCartProduct(recycleView: RecyclerView) {
        this.recycleView = recycleView
        products_cart_recycle_view.layoutManager = LinearLayoutManager(this.context)
    }

    private fun loadProductsAndUpdateMenu(menu:Menu, recycleView: RecyclerView){
        Async {
            val resultQuery = SFApplication.app.dbRoom.productDao().getAll()

            ManageThread.executeOnMainThread(this.context!!){
                loadDataInRecycleView(resultQuery.toMutableList(), recycleView)
                menu.getItem(0).isEnabled = productListAdapter.itemCount > 0
            }
        }.execute()
    }

    /**
     * Charge les données dans la recycleview
     */
    private fun loadDataInRecycleView(product: MutableList<Product>, recycleView: RecyclerView){
        this.updateTextViewEmptyCartListProduct(product)

        productListAdapter.onClickListener = { id ->
            val builder = this.context?.let { AlertDialog.Builder(it) }
            if (builder != null) {
                builder.setTitle(getString(R.string.information))
                builder.setMessage(getString(R.string.delete_product))

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    Async {
                        val productToDelete = SFApplication.app.dbRoom.productDao().getById(id)
                        if (productToDelete != null) {
                            SFApplication.app.dbRoom.productDao().delete(productToDelete)
                            product.remove(productToDelete)

                            ManageThread.executeOnMainThread(this.context!!){
                                this.loadProductsAndUpdateMenu(menu, recycleView)
                            }
                        }
                    }.execute()
                }
                builder.setNegativeButton(android.R.string.no) { dialog, which ->

                }
                builder.show()
            }
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
