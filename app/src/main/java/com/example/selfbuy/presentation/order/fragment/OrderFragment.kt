package com.example.selfbuy.presentation.order.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.resumeOrderList.SFResumeOrderListAdapter
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.room.Async
import com.example.selfbuy.room.entity.Product
import com.example.selfbuy.utils.ManageThread
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : Fragment() {

    private val resumeOrderListAdapter = SFResumeOrderListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_order, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resume_order_recycle_view.apply { getCartProduct(this) }
    }

    /**
     * Recuperer les produits dans la base room pour pouvoir les afficher dans le panier
     */
    private fun getCartProduct(recycleView: RecyclerView) {
        resume_order_recycle_view.layoutManager = LinearLayoutManager(this.context)

        Async {
            val resultQuery = SFApplication.app.dbRoom.productDao().getAll()

            ManageThread.executeOnMainThread(this.context!!){
                loadDataInRecycleView(resultQuery, recycleView)
            }
        }.execute()
    }

    /**
     * Charge les données dans la recycleview
     */
    private fun loadDataInRecycleView(productList: List<Product>, recycleView: RecyclerView){
        resumeOrderListAdapter.updateList(productList)
        recycleView.adapter = resumeOrderListAdapter
    }
}