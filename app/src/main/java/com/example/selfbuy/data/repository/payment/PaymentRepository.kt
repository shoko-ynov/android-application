package com.example.selfbuy.data.repository.payment

import com.example.selfbuy.data.entity.remote.*
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single
import retrofit2.Call

class PaymentRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     * Route permettant de link une carte banquaire a l'utilisateur actif
     */
    fun linkCardToUser(stripeToken: StripeDto): Single<ResultApiDto<CreditCardDto>> = apiManager.linkCardToUser(stripeToken)

    /**
     * Route permettant de lister les cartes de l'utilisateur actif
     */
    fun getUserCards() : Single<ResultApiDto<ArrayList<CreditCardDto>>> = apiManager.getUserCards()

    /**
     * Route permettant de créer le paiement
     */
    fun createPaymentIntent(orderDto: OrderDto) : Single<ResultApiDto<PaymentIntentDto>> = apiManager.createPaymentIntent(orderDto)

    /**
     * Route permettant de supprimer une carte bancaire
     */
    fun deleteCreditCard(cardId: String): Call<Unit> = apiManager.deleteCreditCard(cardId)

    /**
     * Route permettant de changer la carte bancaire par defaut
     */
    fun setDefaultCard(cardId: String): Call<Unit> = apiManager.setDefaultCard(cardId)
}