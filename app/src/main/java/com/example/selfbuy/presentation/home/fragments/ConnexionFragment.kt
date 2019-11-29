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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.UIUtils.InteractionUserUtils
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.entity.remote.Token
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.home.viewModels.ConnexionViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_connexion.*

class ConnexionFragment : Fragment() {

    private lateinit var viewModelConnexion: ConnexionViewModel
    private lateinit var loginPreferences: SharedPreferences
    private lateinit var loginPrefsEditor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(com.example.selfbuy.R.layout.fragment_connexion, container, false)

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
            val token = Token(tokenSaved, refreshTokenSaved)
            CurrentUser.token = token
            view?.let { v -> Snackbar.make(v, CurrentUser.token!!.token, Snackbar.LENGTH_SHORT).show() }
        }
    }

    /**
     * Lie le viewModel au fragment et s'abonne aux differents evenements
     */
    private fun bindViewModelConnexion(){
        viewModelConnexion = ConnexionViewModel()

        viewModelConnexion.userLiveData.observe(viewLifecycleOwner, Observer { result: ResultApi<Token> ->
            activity?.let { InteractionUserUtils.enableInteractionUser(it) }
            progressBar_connexion.visibility = View.GONE

            loginPrefsEditor.putString("token", result.data?.token)
            loginPrefsEditor.putString("refreshToken", result.data?.refreshToken)
            loginPrefsEditor.commit()

            val token = Token(result.data!!.token, result.data.refreshToken)
            CurrentUser.token = token
        })

        viewModelConnexion.errorLiveData.observe(viewLifecycleOwner, Observer {
            activity?.let { InteractionUserUtils.enableInteractionUser(it) }
            progressBar_connexion.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(it)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    override fun onPause() {
        super.onPause()

        activity?.let { InteractionUserUtils.enableInteractionUser(it) }
    }

    /**
     * Abonnement aux évènements de bouton de connexion et d'inscription
     */
    private fun setOnClickListener(){
        btn_login.setOnClickListener{
            if (checkFields()){
                val login = LoginDto(editText_email.text.toString(), editText_password.text.toString())

                activity?.let { InteractionUserUtils.disableInteractionUser(it) }
                progressBar_connexion.visibility = View.VISIBLE

                viewModelConnexion.authenticate(login)
            }
        }

        btn_register.setOnClickListener{

            this.activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.home_activity_fragment_container, HomeFragment())
                ?.commit()

            view?.let { v -> Snackbar.make(v, "Inscription OK", Snackbar.LENGTH_SHORT).show() }
        }
    }

    /**
     * Contrôle des différents champs que l'utilisateur doit renseigner
     */
    private fun checkFields(): Boolean{
        var isValide = true

        if (!Patterns.EMAIL_ADDRESS.matcher(editText_email.text).matches()){
            isValide = false
            editText_email.error = getString(com.example.selfbuy.R.string.mail_invalid)
        }
        if (editText_password.text.trim().toString().isEmpty()){
            isValide = false
            editText_password.error = getString(com.example.selfbuy.R.string.complete_password)
        }
        return isValide
    }
}