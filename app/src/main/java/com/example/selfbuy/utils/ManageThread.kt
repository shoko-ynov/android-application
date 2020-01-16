package com.example.selfbuy.utils

import android.content.Context
import android.os.Handler

object ManageThread {
    fun executeOnMainThread(context: Context, action: () -> Unit){
        val mainHandler = Handler(context.mainLooper)
        val myRunnable = Runnable {
            action()
        }
        mainHandler.post(myRunnable)
    }
}