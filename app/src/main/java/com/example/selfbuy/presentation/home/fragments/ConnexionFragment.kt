package com.example.selfbuy.presentation.home.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.data.entity.remote.ResultApi
import com.example.selfbuy.data.entity.remote.Token
import com.example.selfbuy.data.entity.remote.User
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.home.viewModels.ConnexionViewModel
import com.example.selfbuy.presentation.home.viewModels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_connexion.*

class ConnexionFragment : Fragment() {

    private lateinit var connexionViewModel: ConnexionViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_connexion, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setOnClickListener()
        this.bindConnexionViewModel()
        this.bindUserViewModel()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val tokenSaved = SFApplication.app.loginPreferences.getString("token", "")
        val refreshTokenSaved = SFApplication.app.loginPreferences.getString("refreshToken", "")

        if (!tokenSaved.isNullOrEmpty() && !refreshTokenSaved.isNullOrEmpty()){
            progressBar_connexion.visibility = View.VISIBLE

            val token = Token(tokenSaved.toString(), refreshTokenSaved.toString())
            CurrentUser.token = token
            userViewModel.getCurrentUser()
        }
    }

    /**
     * On s'abonne aux differents evenements de UserViewModel
     */
    private fun bindUserViewModel(){
        userViewModel = UserViewModel()

        userViewModel.userLiveData.observe(this, Observer { result: ResultApi<User> ->
            progressBar_connexion.visibility = View.GONE
            val user = result.data

            //Passer l'utilisateur a l'activity de profile et charger le fragment profile a la place de cette snackbar
            view?.let { v ->
                if (user != null ) {
                    Snackbar.make(v, user.mail, Snackbar.LENGTH_LONG).show()
                }
            }
        })

        userViewModel.errorLiveData.observe(this, Observer { error: Throwable ->
            //si probleme revenir sur le fragment connexion
            progressBar_connexion.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    /**
     * On s'abonne aux differents evenements de ConnexionViewModel
     */
    private fun bindConnexionViewModel(){
        connexionViewModel = ConnexionViewModel()

        connexionViewModel.tokenLiveData.observe(viewLifecycleOwner, Observer { result: ResultApi<Token> ->
            progressBar_connexion.visibility = View.GONE

            SFApplication.app.loginPrefsEditor.putString("token", result.data?.token)
            SFApplication.app.loginPrefsEditor.putString("refreshToken", result.data?.refreshToken)
            SFApplication.app.loginPrefsEditor.commit()

            val token = Token(result.data!!.token, result.data.refreshToken)
            CurrentUser.token = token
        })

        connexionViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            progressBar_connexion.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }

    /**
     * Abonnement aux évènements de bouton de connexion et d'inscription
     */
    private fun setOnClickListener(){
        btn_login.setOnClickListener{
            if (checkFields()){
                val login = LoginDto(editText_email.text.toString(), editText_password.text.toString())

                progressBar_connexion.visibility = View.VISIBLE

                connexionViewModel.authenticate(login)
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
            editText_email.error = getString(R.string.mail_invalid)
        }
        if (editText_password.text.trim().toString().isEmpty()){
            isValide = false
            editText_password.error = getString(R.string.complete_password)
        }
        return isValide
    }
}