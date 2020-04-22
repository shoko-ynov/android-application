package com.example.selfbuy.presentation.creditCard.fragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.creditCardList.SFCreditCardListAdapter
import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.order.viewModel.CreditCardViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_list_credits_cards.*

class ListCreditsCardFragment : Fragment() {

    private val creditCardViewModel = CreditCardViewModel()
    private val creditCardListAdapter = SFCreditCardListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_list_credits_cards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add_credits_card.setOnClickListener {
            progressBar_list_credits_cards.visibility = View.VISIBLE
        }

        progressBar_list_credits_cards.background = null
        credits_cards_recycle_view.apply { bindCreditCardViewModel(this) }
        this.setSwipeRefreshListener()
        this.loadProducts()
    }

    /**
     * On s'abonne aux differents evenements de HomeViewModel
     */
    private fun bindCreditCardViewModel(recycleView: RecyclerView) {
        creditCardViewModel.creditCardsLiveData.observe(
            viewLifecycleOwner,
            Observer { resultDto: ResultApiDto<ArrayList<CreditCardDto>> ->
                progressBar_list_credits_cards.visibility = View.GONE

                credits_cards_recycle_view.layoutManager = GridLayoutManager(this.context, 1)

                if (resultDto.data != null) {
                    updateList(resultDto.data)
                    /*productListAdapter.onClickListener = { id ->
                        val intent = Intent(this.context, DetailProductActivity::class.java)
                        intent.putExtra("idProduct", id)
                        startActivity(intent)
                    } */

                    recycleView.adapter = creditCardListAdapter
                }
            })

        creditCardViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_list_credits_cards.visibility = View.GONE
            credits_cards_refresh_layout.isRefreshing = false

            this.updateTextViewEmptyListCreditCard(creditCardListAdapter.list)

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    /**
     * Met a jour la liste des cartes de credits dans l'adapter
     */
    private fun updateList(creditCardsList: List<CreditCardDto>) {
        credits_cards_refresh_layout.isRefreshing = false
        creditCardListAdapter.updateList(creditCardsList)

        this.updateTextViewEmptyListCreditCard(creditCardsList)
    }

    /**
     * Met à jour le textview indiquant que la liste de cartes de credits est vide
     */
    private fun updateTextViewEmptyListCreditCard(creditCardsList: List<CreditCardDto>){
        if(creditCardsList.any()){
            twEmptyListCreditsCards.visibility = TextView.INVISIBLE
        } else {
            twEmptyListCreditsCards.visibility = TextView.VISIBLE
        }
    }

    /**
     * Charge la liste de creditCardsList
     */
    private fun loadProducts() {
        progressBar_list_credits_cards.visibility = View.VISIBLE
        val creditCardsList: List<CreditCardDto> = emptyList()
        creditCardListAdapter.updateList(creditCardsList)

        creditCardViewModel.getUserCards()
    }

    /**
     * Gere le swipe refresh de la liste des creditCardsList
     */
    private fun setSwipeRefreshListener() {
        credits_cards_refresh_layout.setOnRefreshListener {
            this.loadProducts()
        }
    }
}