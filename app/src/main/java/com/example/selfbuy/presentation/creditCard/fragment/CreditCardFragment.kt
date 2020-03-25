package com.example.selfbuy.presentation.creditCard.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.selfbuy.R
import kotlinx.android.synthetic.main.fragment_credit_card.*

class CreditCardFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.validate_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.check_menu_validate) {
            val params = cardInputWidget.paymentMethodCreateParams
            if (params != null) {

                val cardValues = params.toParamMap().entries.find {
                    it.key == "card"
                }
                val creditCardValues = cardValues?.value as Map<*, *>

                var cvc = ""
                var exp_month = ""
                var exp_year = ""
                var number = ""

                for ((key, value) in creditCardValues) {
                    when (key) {
                        "cvc" -> cvc = value.toString()
                        "exp_month" -> exp_month = value.toString()
                        "exp_year" -> exp_year = value.toString()
                        "number" -> number = value.toString()
                    }
                }

                Toast.makeText(this.context!!, "$number - $exp_month - $exp_year - $cvc", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_credit_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardInputWidget.setShouldShowPostalCode(false)
    }
}