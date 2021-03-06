package com.example.selfbuy.data.repository.connexion

import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.local.Login
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single

class ConnexionRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     *  Connecte l'utilisateur passé en parametre et renvois les données de l'utilisateur si pas d'erreur
     */
    fun authenticate(login: Login): Single<ResultApiDto<TokenDto>> = apiManager.authenticate(login)
}