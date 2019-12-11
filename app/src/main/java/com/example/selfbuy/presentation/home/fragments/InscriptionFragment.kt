package com.example.selfbuy.presentation.home.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
//import com.example.selfbuy.UIUtils.InteractionUserUtils
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.data.entity.local.InscriptionDto
import com.example.selfbuy.data.entity.remote.ResultApiDto


import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.home.viewModels.InscriptionViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_connexion.*
import kotlinx.android.synthetic.main.fragment_connexion.progressBar_connexion
import kotlinx.android.synthetic.main.fragment_inscription.*

class InscriptionFragment : Fragment() {


    private lateinit var viewModelConnexion: InscriptionViewModel
    private lateinit var loginPreferences: SharedPreferences
    private lateinit var loginPrefsEditor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(com.example.selfbuy.R.layout.fragment_inscription, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setOnClickListener()
        this.bindViewModelConnexion()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loginPreferences = activity!!.getSharedPreferences("loginPrefs", AppCompatActivity.MODE_PRIVATE)
        loginPrefsEditor = loginPreferences.edit()

        val tokenSaved = loginPreferences.getString("token", "")
        val refreshTokenSaved = loginPreferences.getString("refreshToken", "")

        if (!tokenSaved.isNullOrEmpty() && !refreshTokenSaved.isNullOrEmpty()){
            val token = TokenDto(tokenSaved, refreshTokenSaved)
            CurrentUser.tokenDto = token
            view?.let { v -> Snackbar.make(v, CurrentUser.tokenDto!!.token, Snackbar.LENGTH_SHORT).show() }
        }
    }

    /**
     * Lie le viewModel au fragment et s'abonne aux differents evenements
     */
    private fun bindViewModelConnexion(){
        viewModelConnexion = InscriptionViewModel()

        viewModelConnexion.userLiveData.observe(viewLifecycleOwner, Observer { result: ResultApiDto<TokenDto> ->
            //activity?.let { InteractionUserUtils.enableInteractionUser(it) }
            progressBar_connexion.visibility = View.GONE

            val builder = AlertDialog.Builder(this.context!!)

            with(builder)
            {

                setTitle("Finaliser l'inscription")
                setMessage("Vous allez recevoir un e-mail de confirmation pour saisir votre mot de passe, " +
                        "une fois que ceci sera fait vous pourrez désormais vous connecter avec vos identifiants")

                builder.setNeutralButton(
                    "Connexion"
                ) { dialog, which ->

                    val connexionFragment = ConnexionFragment()
                    val fragmentTransaction = activity!!.supportFragmentManager.beginTransaction().apply {
                        replace(R.id.home_activity_fragment_container,connexionFragment)
                        addToBackStack(null)
                    }
                    fragmentTransaction.commit()

                }
                builder.setNegativeButton(
                    "Annuler"
                ) { dialog, which ->

                    //ne fait rien
                }
                show()

            }
        })



        /*
        loginPrefsEditor.putString("token", result.data?.token)
        loginPrefsEditor.putString("refreshToken", result.data?.refreshToken)
        loginPrefsEditor.commit()

        val token = Token(result.data!!.token, result.data.refreshToken)
        CurrentUser.token = token

        view?.let { v -> Snackbar.make(v, CurrentUser.token!!.token, Snackbar.LENGTH_SHORT).show() }*/


        viewModelConnexion.errorLiveData.observe(viewLifecycleOwner, Observer {
            //activity?.let { InteractionUserUtils.enableInteractionUser(it) }
            progressBar_connexion.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(it)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    override fun onPause() {
        super.onPause()

        //activity?.let { InteractionUserUtils.enableInteractionUser(it) }
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
                    val inscription = InscriptionDto(editText_email_inscription.text.toString())

                    //activity?.let { InteractionUserUtils.disableInteractionUser(it) }
                    progressBar_connexion.visibility = View.VISIBLE

                    viewModelConnexion.inscription(inscription)
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
}