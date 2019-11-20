package com.example.selfbuy.data.repository.connexion

import com.example.selfbuy.data.entity.remote.Token
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single

class ConnexionRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     *  Connecte l'utilisateur passé en parametre et renvois les données de l'utilisateur si pas d'erreur
     */
    fun authenticate(loginDto: LoginDto): Single<ResultApi<Token>> = apiManager.authenticate(loginDto)
}