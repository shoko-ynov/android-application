package com.example.selfbuy.data.manager

import com.example.selfbuy.data.entity.local.Inscription
import com.example.selfbuy.data.entity.local.Login
import com.example.selfbuy.data.entity.remote.*
import com.example.selfbuy.data.manager.api.BaseApi.API_BASE_URL
import com.example.selfbuy.data.manager.service.InterceptorService
import com.example.selfbuy.data.manager.service.ApiService
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiManager {

    private var client = OkHttpClient.Builder()
        .addInterceptor(InterceptorService())
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val service: ApiService = Retrofit.Builder()
        .baseUrl(API_BASE_URL).client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)

    /**
     *  Permet de se connecter et retourne l'utilisateur connecté si il n'y a pas d'erreur
     */
    fun authenticate(login: Login): Single<ResultApiDto<TokenDto>> = service.authenticate(login)

    /**
     * Route permettant de récupérer les informations de l'utilisateur connecté en fonction de son token
     */
    fun getCurrentUser() : Single<ResultApiDto<UserDto>> = service.getCurrentUser()

    /**
     * Route permettant de récupérer la liste de produit
     */
    fun getProducts() : Single<ResultApiDto<ArrayList<ProductDto>>> = service.getProducts()

    /**
     * Route permettant de récupérer le détail d'un produit
     */
    fun getProductById(idProduct : String) : Single<ResultApiDto<ProductDto>> = service.getProductById(idProduct)

    /**
     *  Permet l'inscription d'un utilisateur avec l'email passé en parametre
     */
    fun inscription(inscription: Inscription):Single<ResultApiDto<InscriptionDto>> = service.inscription(inscription)

    /**
     * Route pour la mise à jour du profile utilisateur
     */
    fun putUserById(idUser: String, user: UserDto): Call<Unit> = service.putUserById(idUser, user)

    /**
     * Route permettant de link une carte banquaire a l'utilisateur actif
     */
    fun linkCardToUser(stripeToken: StripeDto): Call<Unit> = service.linkCardToUser(stripeToken)

    /**
     * Route permettant de lister les cartes de l'utilisateur actif
     */
    fun getUserCards() : Single<ResultApiDto<ArrayList<CreditCardDto>>> = service.getUserCards()

    /**
     * Route permettant de créer le paiement
     */
    fun createPaymentIntent(orderDto: OrderDto) : Single<ResultApiDto<PaymentIntentDto>> = service.createPaymentIntent(orderDto)

    /**
     * Route permettant de supprimer une carte bancaire
     */
    fun deleteCreditCard(cardId: String): Call<Unit> = service.deleteCreditCard(cardId)
}