package com.example.selfbuy.adapters.creditCardList.viewHolder

import android.annotation.SuppressLint
import android.view.View
import android.widget.RadioButton
import com.example.selfbuy.R
import com.example.selfbuy.adapters.common.viewHolders.SFListAdapterViewHolder
import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.stripe.android.model.CardBrand
import com.stripe.android.model.PaymentMethod
import kotlinx.android.synthetic.main.credit_card_cell_layout.view.*
import java.util.*

class SFCreditsCardsViewHolder(itemView: View): SFListAdapterViewHolder<CreditCardDto, String>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun bind(entity: CreditCardDto, onClick: (String) -> Unit) {
        val positionCreditCard = layoutPosition + 1
        itemView.tw_credit_card_index.text = "${itemView.resources.getString(R.string.card_index)} $positionCreditCard"
        itemView.tw_credit_card_number.text = "${itemView.resources.getString(R.string.stars_cb_number)} \r\r\r\r\r\r ${entity.last4}"

        val brand: String = if(entity.brand.isNullOrEmpty()){
            PaymentMethod.Card.Brand.UNKNOWN
        } else{
            entity.brand
        }
        setBrandImage(brand, itemView)

        if(entity.name?.trim().isNullOrEmpty()){
            itemView.tw_credit_card_name.text = itemView.resources.getString(R.string.unspecified)
        }
        else{
            itemView.tw_credit_card_name.text = entity.name
        }

        itemView.tw_credit_card_expire.text = "${entity.expMonth}/${entity.expYear}"
        ManageRadioButton.listItemView.add(itemView)

        itemView.radioButton.setOnClickListener {
            ManageRadioButton.updateSelectedRadioButton(itemView)
        }

        itemView.btn_remove_from_credits_cards.setOnClickListener {
            onClick(entity._id)
        }

        if(entity.isDefaultCard){
            ManageRadioButton.updateSelectedRadioButton(itemView)
        }
    }

    private fun setBrandImage(card: String, itemView: View){
        when (card.toLowerCase(Locale.ROOT)){
            PaymentMethod.Card.Brand.AMERICAN_EXPRESS.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.AmericanExpress.icon)
            PaymentMethod.Card.Brand.DINERS_CLUB.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.DinersClub.icon)
            PaymentMethod.Card.Brand.DISCOVER.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.Discover.icon)
            PaymentMethod.Card.Brand.JCB.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.JCB.icon)
            PaymentMethod.Card.Brand.MASTERCARD.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.MasterCard.icon)
            PaymentMethod.Card.Brand.UNIONPAY.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.UnionPay.icon)
            PaymentMethod.Card.Brand.UNKNOWN.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.Unknown.icon)
            PaymentMethod.Card.Brand.VISA.toLowerCase(Locale.ROOT) ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.Visa.icon)
            else ->
                itemView.img_credit_card_brand.setImageResource(CardBrand.Unknown.icon)
        }
    }
}

object ManageRadioButton{
    val listItemView: MutableList<View> = arrayListOf()
    private lateinit var selectedItemView: View

    fun updateSelectedRadioButton(itemView: View){
        selectedItemView = itemView.radioButton
        listItemView.forEach {
            if(it == itemView){
                it.radioButton.isChecked = true
                it.tw_credit_card_default.text = "(Défaut)"
            }
            else{
                it.radioButton.isChecked = false
                it.tw_credit_card_default.text = ""
            }
        }
    }

    fun getSelectedItemView(): View{
        return selectedItemView
    }
}