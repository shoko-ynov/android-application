package com.example.selfbuy.data.repository.payment

import com.example.selfbuy.data.entity.remote.StripeDto
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import retrofit2.Call

class PaymentRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     * Route permettant de link une carte banquaire a l'utilisateur actif
     */
    fun linkCardToUser(stripeToken: StripeDto): Call<Unit> = apiManager.linkCardToUser(stripeToken)
}