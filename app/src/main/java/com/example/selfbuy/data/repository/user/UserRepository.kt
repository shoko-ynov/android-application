package com.example.selfbuy.data.repository.user

import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.data.manager.ApiManager
import com.example.selfbuy.data.repository.DataRepository
import io.reactivex.Single
import retrofit2.Call

class UserRepository(apiManager: ApiManager) : DataRepository(apiManager) {

    /**
     * Route permettant de récupérer les informations de l'utilisateur connecté en fonction de son token
     */
    fun getCurrentUser() : Single<ResultApiDto<UserDto>> = apiManager.getCurrentUser()

    /**
     * Route pour la mise à jour du profile utilisateur
     */
    fun putUserById(idUser: String, user: UserDto): Call<Unit> = apiManager.putUserById(idUser, user)
}