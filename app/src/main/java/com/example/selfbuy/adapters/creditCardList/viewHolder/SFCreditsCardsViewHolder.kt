package com.example.selfbuy.adapters.creditCardList.viewHolder

import android.annotation.SuppressLint
import android.view.View
import android.widget.RadioButton
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.viewHolders.SFListAdapterViewHolder
import com.example.selfbuy.data.entity.remote.CreditCardDto
import kotlinx.android.synthetic.main.credit_card_cell_layout.view.*

class SFCreditsCardsViewHolder(itemView: View): SFListAdapterViewHolder<CreditCardDto, String>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun bind(entity: CreditCardDto, onClick: (String) -> Unit) {
        val positionCreditCard = layoutPosition + 1
        itemView.tw_credit_card_index.text = "${itemView.resources.getString(R.string.card_index)} $positionCreditCard"
        itemView.tw_credit_card_number.text = "${itemView.resources.getString(R.string.stars_cb_number)} \r\r\r\r\r\r ${entity.last4}"

        if(entity.name.isNullOrEmpty()){
            itemView.tw_credit_card_name.text = itemView.resources.getString(R.string.unspecified)
        }
        else{
            itemView.tw_credit_card_name.text = entity.name
        }

        itemView.tw_credit_card_expire.text = "${entity.expMonth}/${entity.expYear}"
        ManageRadioButton.listRadioButton.add(itemView.radioButton)

        itemView.radioButton.setOnClickListener {
            ManageRadioButton.updateSelectedRadioButton(itemView.radioButton)
        }

        itemView.btn_remove_from_credits_cards.setOnClickListener {
            onClick(entity._id)
        }

        if(entity.isDefaultCard){
            ManageRadioButton.updateSelectedRadioButton(itemView.radioButton)
        }
    }
}

object ManageRadioButton{
    val listRadioButton: MutableList<RadioButton> = arrayListOf()
    private lateinit var selectedRadioButton: RadioButton

    fun updateSelectedRadioButton(radioButton: RadioButton){
        selectedRadioButton = radioButton
        listRadioButton.forEach {
            it.isChecked = it == radioButton
        }
    }

    fun getSelectedRadioButton(): RadioButton{
        return selectedRadioButton
    }
}