package com.example.selfbuy.data.repository.connexion

import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.local.InscriptionDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single

class InscriptionRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     *  Connecte l'utilisateur passé en parametre et renvois les données de l'utilisateur si pas d'erreur
     */
    fun inscription( inscriptionDto: InscriptionDto): Single<ResultApiDto<TokenDto>> = apiManager.inscription(inscriptionDto)
}