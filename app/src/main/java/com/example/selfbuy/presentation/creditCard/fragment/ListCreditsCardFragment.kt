package com.example.selfbuy.presentation.creditCard.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.adapters.creditCardList.SFCreditCardListAdapter
import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.creditCard.activity.CreditCardActivity
import com.example.selfbuy.presentation.order.viewModel.CreditCardViewModel
import com.google.android.material.snackbar.Snackbar
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
            val intent = Intent(this.context, CreditCardActivity::class.java)
            startActivity(intent)
        }

        progressBar_list_credits_cards.background = null
        credits_cards_recycle_view.apply { bindCreditCardViewModel(this) }
    }

    override fun onResume() {
        super.onResume()

        this.loadCreditsCards()
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
                    creditCardListAdapter.onClickListener = { cardId ->
                        progressBar_list_credits_cards.visibility = View.VISIBLE
                        this.popUpValidateDeleteCreditCard(cardId)
                    }

                    recycleView.adapter = creditCardListAdapter
                }
            })

        creditCardViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_list_credits_cards.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }

            this.loadCreditsCards()
        })

        creditCardViewModel.deleteCreditCard.observe(viewLifecycleOwner, Observer {
            progressBar_list_credits_cards.visibility = View.GONE

            Toast.makeText(this.context!!, getString(R.string.credit_card_deleted_sucessfully), Toast.LENGTH_SHORT).show()
            this.loadCreditsCards()
        })
    }

    /**
     * Met a jour la liste des cartes de credits dans l'adapter
     */
    private fun updateList(creditCardsList: List<CreditCardDto>) {
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
    private fun loadCreditsCards() {
        progressBar_list_credits_cards.visibility = View.VISIBLE
        val creditCardsList: List<CreditCardDto> = emptyList()
        creditCardListAdapter.updateList(creditCardsList)

        creditCardViewModel.getUserCards()
    }

    /**
     * Affiche la popup validation suppression cb
     */
    private fun popUpValidateDeleteCreditCard(cardId:String){
        val builder = AlertDialog.Builder(this.context!!)

        with(builder)
        {
            setTitle(getString(R.string.delete_card))
            setMessage(getString(R.string.confirm_delete_card))

            builder.setNeutralButton(
                getString(R.string.yes)
            ) { _, _ ->
                creditCardViewModel.deleteCreditCard(cardId)
            }
            builder.setNegativeButton(
                getString(R.string.no)
            ) { _, _ ->
                //Ne pas supprimer
                progressBar_list_credits_cards.visibility = View.GONE
            }
            show()
        }
    }
}