package com.example.selfbuy.presentation.creditCard.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.StripeDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.creditCard.viewModel.StripeViewModel
import com.example.selfbuy.presentation.profile.fragments.ProfileFragment
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentAuthConfig
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.fragment_connexion.*
import kotlinx.android.synthetic.main.fragment_credit_card.*

class CreditCardFragment : Fragment() {

    private val stripeViewModel = StripeViewModel()

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
                var expMonth = ""
                var expYear = ""
                var number = ""

                for ((key, value) in creditCardValues) {
                    when (key) {
                        "cvc" -> cvc = value.toString()
                        "exp_month" -> expMonth = value.toString()
                        "exp_year" -> expYear = value.toString()
                        "number" -> number = value.toString()
                    }
                }
                progressBar_add_card.visibility = View.VISIBLE

                val stripe = Stripe(this.context!!, PaymentConfiguration.getInstance(this.context!!).publishableKey)
                val card = Card.create(number, expMonth.toInt(), expYear.toInt(), cvc)
                stripe.createCardToken(
                    card,
                    callback = object : ApiResultCallback<Token> {
                        override fun onError(e: Exception) {
                            progressBar_add_card.visibility = View.GONE

                            view?.let { v -> Snackbar.make(v, e.localizedMessage, Snackbar.LENGTH_LONG).show() }
                        }
                        override fun onSuccess(result: Token) {
                            stripeViewModel.linkCardToUser(StripeDto(result.id))
                        }
                    }
                )
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
        this.bindStripeViewModel()
    }

    private fun bindStripeViewModel(){
        stripeViewModel.stripeDtoLiveData.observe(viewLifecycleOwner, Observer {
            progressBar_add_card.visibility = View.GONE

            Toast.makeText(this.context!!, getString(R.string.credit_card_saved), Toast.LENGTH_SHORT).show()
            this.activity?.finish()
        })

        stripeViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_add_card.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }
}