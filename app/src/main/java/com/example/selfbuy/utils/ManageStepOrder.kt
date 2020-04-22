package com.example.selfbuy.utils

import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.selfbuy.R
import java.lang.Exception

object ManageStepOrder {
    private var position:Int = 0
    private var countSteps: Int = 3
    private var isInit: Boolean = false

    fun initStep(activity: AppCompatActivity){
        position = 0
        countSteps = 3
        this.updateStepperOrderImages(activity)
        isInit = true
    }

    fun nextStep(activity: AppCompatActivity){
        if(isInit){
            if(position < countSteps){
                position++
                this.updateStepperOrderImages(activity)
            }
        }
        else{
            throw Exception(activity.getString(R.string.manage_step_order_init))
        }
    }

    fun previousStep(activity: AppCompatActivity){
        if(isInit){
            if(position > 0){
                position--
                this.updateStepperOrderImages(activity)
            }
        }
        else{
            throw Exception(activity.getString(R.string.manage_step_order_init))
        }
    }

    private fun updateStepperOrderImages(activity: AppCompatActivity){
        when(position){
            0 -> this.setInitStep(activity)
            1 -> this.setSecondStep(activity)
            2 -> this.setLastStep(activity)
        }
    }

    private fun setInitStep(activity: AppCompatActivity){
        activity.findViewById<ImageView>(R.id.img_step1_orange).setImageResource(R.drawable.ic_step1_orange)
        activity.findViewById<ImageView>(R.id.img_trait_gris_01).setImageResource(R.drawable.ic_trait_gris)
        activity.findViewById<ImageView>(R.id.img_step2_gris).setImageResource(R.drawable.ic_step2_gris)
        activity.findViewById<ImageView>(R.id.img_trait_gris_02).setImageResource(R.drawable.ic_trait_gris)
        activity.findViewById<ImageView>(R.id.img_step3_gris).setImageResource(R.drawable.ic_step3_gris)
    }

    private fun setSecondStep(activity: AppCompatActivity){
        activity.findViewById<ImageView>(R.id.img_step1_orange).setImageResource(R.drawable.ic_step_valide)
        activity.findViewById<ImageView>(R.id.img_trait_gris_01).setImageResource(R.drawable.ic_trait_orange)
        activity.findViewById<ImageView>(R.id.img_step2_gris).setImageResource(R.drawable.ic_step2_orange)
        activity.findViewById<ImageView>(R.id.img_trait_gris_02).setImageResource(R.drawable.ic_trait_gris)
        activity.findViewById<ImageView>(R.id.img_step3_gris).setImageResource(R.drawable.ic_step3_gris)
    }

    private fun setLastStep(activity: AppCompatActivity){
        activity.findViewById<ImageView>(R.id.img_step1_orange).setImageResource(R.drawable.ic_step_valide)
        activity.findViewById<ImageView>(R.id.img_trait_gris_01).setImageResource(R.drawable.ic_trait_orange)
        activity.findViewById<ImageView>(R.id.img_step2_gris).setImageResource(R.drawable.ic_step_valide)
        activity.findViewById<ImageView>(R.id.img_trait_gris_02).setImageResource(R.drawable.ic_trait_orange)
        activity.findViewById<ImageView>(R.id.img_step3_gris).setImageResource(R.drawable.ic_step3_orange)
    }
}