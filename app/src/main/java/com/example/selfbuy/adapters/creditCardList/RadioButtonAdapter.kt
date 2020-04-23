package com.example.selfbuy.adapters.creditCardList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.stripe.android.model.CardBrand
import com.stripe.android.model.PaymentMethod
import kotlinx.android.synthetic.main.credit_card_cell_layout.view.*
import java.util.*

class RadioButtonAdapter : RecyclerView.Adapter<RadioButtonAdapter.ViewHolder>() {
    private var lastSelectedPosition = -1
    private var defaultCreditCard: CreditCardDto? = null
    private var isFirstLoad = false
    private val creditsCards: MutableList<CreditCardDto> = arrayListOf()
    var onClickListener: (String?) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.credit_card_cell_layout , parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val entity: CreditCardDto = creditsCards[position]

        val cardPositionNumber = position + 1
        holder.itemView.tw_credit_card_index.text = "${holder.itemView.resources.getString(R.string.card_index)} $cardPositionNumber"
        holder.itemView.tw_credit_card_number.text = "${holder.itemView.resources.getString(R.string.stars_cb_number)} \r\r\r\r\r\r ${entity.last4}"

        val brand: String = if(entity.brand.isNullOrEmpty()){
            PaymentMethod.Card.Brand.UNKNOWN
        } else{
            entity.brand
        }
        setBrandImage(brand, holder.itemView)

        if(entity.name?.trim().isNullOrEmpty()){
            holder.itemView.tw_credit_card_name.text = holder.itemView.resources.getString(R.string.unspecified)
        }
        else{
            holder.itemView.tw_credit_card_name.text = entity.name
        }

        holder.itemView.tw_credit_card_expire.text = "${entity.expMonth}/${entity.expYear}"

        //since only one radio button is allowed to be selected,
        // this condition un-checks previous selections
        defaultCreditCard = creditsCards.find { it.isDefaultCard }

        if(!isFirstLoad){
            lastSelectedPosition = creditsCards.indexOf(defaultCreditCard)
            isFirstLoad = true
        }

        holder.selectionState.isChecked = lastSelectedPosition == position

        holder.bind(entity, onClickListener)
    }

    fun updateList(list: List<CreditCardDto>) {
        this.creditsCards.clear()
        this.creditsCards.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return creditsCards.size
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var selectionState: RadioButton = view.radioButton
        var btnDelete: ImageButton = view.btn_remove_from_credits_cards

        fun bind(entity: CreditCardDto, onClick: (String?) -> Unit){
            btnDelete.setOnClickListener {
                isFirstLoad = false
                onClick(entity._id)
            }

            selectionState.setOnClickListener {
                if(lastSelectedPosition != adapterPosition){
                    lastSelectedPosition = adapterPosition
                    notifyDataSetChanged()
                    onClick("${ManageRadioButton.RADIO_BUTTON}${entity._id}")
                }
            }
        }
    }
}

object ManageRadioButton{
    const val RADIO_BUTTON = "RADIO_BUTTON_"
}