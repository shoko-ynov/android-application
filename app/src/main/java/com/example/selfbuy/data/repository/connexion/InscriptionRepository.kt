package com.example.selfbuy.data.repository.connexion

import com.example.selfbuy.data.entity.local.Inscription
import com.example.selfbuy.data.entity.remote.InscriptionDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single

class InscriptionRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     *  Permet l'inscription d'un utilisateur avec l'email passé en parametre
     */
    fun inscription(inscription: Inscription): Single<ResultApiDto<InscriptionDto>> = apiManager.inscription(inscription)
}