package com.example.selfbuy.data.manager.service

import com.example.selfbuy.data.entity.local.Inscription
import com.example.selfbuy.data.entity.local.Login
import com.example.selfbuy.data.entity.remote.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /**
     *  Route pour l'authentification
     */
    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("api/auth")
    fun authenticate(@Body login: Login) : Single<ResultApiDto<TokenDto>>

    /**
     * Route pour le refreshToken
     */
    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("api/auth/refresh")
    fun refreshToken(@Body refreshToken: TokenDto) : Call<ResultApiDto<TokenDto>>

    /**
     * Route permettant de récupérer les informations de l'utilisateur connecté en fonction de son token
     */
    @GET("api/me")
    fun getCurrentUser() : Single<ResultApiDto<UserDto>>

    /**
     * Route permettant de récupérer la liste de produit
     */
    @GET("api/products")
    fun getProducts() : Single<ResultApiDto<ArrayList<ProductDto>>>

    /**
     * Route permettant de récupérer le détail d'un produit
     */
    @GET("api/products/{idProduct}")
    fun getProductById(@Path("idProduct") idProduct: String) : Single<ResultApiDto<ProductDto>>

    /**
     * Route pour l'inscription
     */
    @POST("api/users")
    fun inscription(@Body inscription: Inscription) : Single<ResultApiDto<InscriptionDto>>

    /**
     * Route pour la mise à jour du profile utilisateur
     */
    @PUT("api/users/{idUser}")
    fun putUserById(@Path("idUser") idUser:String, @Body user: UserDto): Call<Unit>

    /**
     * Route permettant de link une carte banquaire a l'utilisateur actif
     */
    @Headers("Content-Type:application/json; charset=UTF-8")
    @POST("api/stripe/users/card")
    fun linkCardToUser(@Body stripeToken: StripeDto): Call<Unit>
}