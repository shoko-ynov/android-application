package com.example.selfbuy.presentation.home.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.local.Inscription
import com.example.selfbuy.data.entity.remote.InscriptionDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils


import com.example.selfbuy.presentation.home.viewModels.InscriptionViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_inscription.progressBar_inscription
import kotlinx.android.synthetic.main.fragment_inscription.*

class InscriptionFragment : Fragment() {


    private lateinit var viewModelInscription: InscriptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(com.example.selfbuy.R.layout.fragment_inscription, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setOnClickListener()
        this.bindViewModelInscription()
    }



    /**
     * Lie le viewModel au fragment et s'abonne aux differents evenements
     */
    private fun bindViewModelInscription(){
        viewModelInscription = InscriptionViewModel()
        viewModelInscription.userLiveData.observe(viewLifecycleOwner, Observer { resultInscriptionDto: ResultApiDto<InscriptionDto> ->
            progressBar_inscription.visibility = View.GONE


            if (resultInscriptionDto.data!=null) {

                println("user exist : ${resultInscriptionDto.data.userExist}")
                println("mail sent : ${resultInscriptionDto.data.mailIsSent}")

                if (!resultInscriptionDto.data.userExist) {
                    popUpInsriptionSuccess()
                } else {
                    popUpInscriptionFailded()
                }
            }
        })

        viewModelInscription.errorLiveData.observe(viewLifecycleOwner, Observer {
            progressBar_inscription.visibility = View.GONE
            val errorBodyApi = ErrorUtils.getErrorApi(it)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * Abonnement aux évènements de bouton de connexion et d'inscription
     */
    private fun setOnClickListener(){
        btn_inscription.setOnClickListener{
            if (checkFields()){
                val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                val isConnected: Boolean = activeNetwork?.isConnected == true

                if(!isConnected){
                    view?.let { v -> Snackbar.make(v, "Pas d'internet", Snackbar.LENGTH_SHORT).show() }
                }
                else{
                    val inscription = Inscription(editText_email_inscription.text.toString())
                    progressBar_inscription.visibility = View.VISIBLE
                    viewModelInscription.inscription(inscription)
                }
            }
        }
    }

    /**
     * Contrôle des différents champs que l'utilisateur doit renseigner
     */
    private fun checkFields(): Boolean{
        var isValide = true

        if (!Patterns.EMAIL_ADDRESS.matcher(editText_email_inscription.text).matches()){
            isValide = false
            editText_email_inscription.error = getString(com.example.selfbuy.R.string.mail_invalid)
        }
        return isValide
    }

    private fun popUpInsriptionSuccess(){

        val builder = AlertDialog.Builder(this.context!!)

        with(builder)
        {

            setTitle(getString(R.string.finish_register))
            setMessage(getString(R.string.popUp_message_OK))

            builder.setNeutralButton(
                getString(R.string.button_popUp_OK)
            ) { dialog, which ->
                val connexionFragment = ConnexionFragment()
                val fragmentTransaction =
                    activity!!.supportFragmentManager.beginTransaction().apply {
                        replace(R.id.home_activity_fragment_container, connexionFragment)
                        addToBackStack(null)
                    }
                fragmentTransaction.commit()

            }
            builder.setNegativeButton(
                getString(R.string.button_popUp_cancel)
            ) { dialog, which ->
                //ne fait rien
            }
            show()

        }

    }


    private fun popUpInscriptionFailded(){

        val builder = AlertDialog.Builder(this.context!!)

        with(builder)
        {

            setTitle(getString(R.string.failed_inscription_title))
            setMessage(getString(R.string.popUp_message_NOT_OK))

            builder.setNeutralButton(
                getString(R.string.button_popUp_OK)
            ) { dialog, which ->
                editText_email_inscription.setText("")
            }
            builder.setNegativeButton(
                getString(R.string.button_popUp_cancel)
            ) { dialog, which ->
                //ne fait rien
            }
            show()

        }
    }
}