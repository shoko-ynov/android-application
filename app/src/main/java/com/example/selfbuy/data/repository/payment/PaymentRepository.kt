package com.example.selfbuy.data.repository.payment

import com.example.selfbuy.data.entity.remote.CreditCardDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.StripeDto
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single
import retrofit2.Call

class PaymentRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     * Route permettant de link une carte banquaire a l'utilisateur actif
     */
    fun linkCardToUser(stripeToken: StripeDto): Call<Unit> = apiManager.linkCardToUser(stripeToken)

    /**
     * Route permettant de lister les cartes de l'utilisateur actif
     */
    fun getUserCards() : Single<ResultApiDto<ArrayList<CreditCardDto>>> = apiManager.getUserCards()
}