package com.example.selfbuy.adapters.creditCardList

import android.view.View
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.SFListAdapter
import com.example.selfbuy.adapters.creditCardList.viewHolder.SFCreditsCardsViewHolder
import com.example.selfbuy.data.entity.remote.CreditCardDto

class SFCreditCardListAdapter: SFListAdapter<CreditCardDto, String, SFCreditsCardsViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.credit_card_cell_layout
    }

    override fun getViewHolder(view: View): SFCreditsCardsViewHolder = SFCreditsCardsViewHolder(view)
}