package com.example.selfbuy.presentation.home.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.productCartList.SFProductCartListAdapter
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageThread
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment() {
    private val productListAdapter = SFProductCartListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.validate_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.check_menu_validate) {
            Toast.makeText(this.context, "Valider panier", Toast.LENGTH_SHORT).show()
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
     * Recuperer les produits dans la base room pour pouvoir les afficher dans le panierx
     */
    private fun getCartProduct(recycleView: RecyclerView) {
        products_cart_recycle_view.layoutManager = LinearLayoutManager(this.context)

        Async {
            val resultQuery = SFApplication.app.dbRoom.productDao().getAll()

            ManageThread.executeOnMainThread(this.context!!){
                loadDataInRecycleView(resultQuery.toMutableList(), recycleView)
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
                                this.updateTextViewEmptyCartListProduct(product)
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
