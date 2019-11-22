package com.example.selfbuy.UIUtils

import android.app.Activity
import android.view.WindowManager

/**
 * Permet de gérer les différentes interactions utilisateurs de manière générale
 */
object InteractionUserUtils {

    /**
     * Autorise les interactions utilisateurs sur l'activité en cours
     */
    fun enableInteractionUser(activity: Activity){
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    /**
     * N'autorise pas les interactions utilisateurs sur l'activité en cours
     */
    fun disableInteractionUser(activity: Activity){
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}