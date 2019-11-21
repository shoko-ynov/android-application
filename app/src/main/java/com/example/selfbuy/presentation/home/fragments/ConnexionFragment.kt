package com.example.selfbuy.presentation.home.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.data.entity.local.LoginDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.home.viewModels.ConnexionViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_connexion.*

class ConnexionFragment : Fragment() {

    private var isBusy: Boolean = false
    private lateinit var viewModelConnexion: ConnexionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(com.example.selfbuy.R.layout.fragment_connexion, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.setOnClickListener()
        this.bindViewModelConnexion()
    }

    /**
     * Lie le viewModel au fragment et s'abonne aux differents evenements
     */
    private fun bindViewModelConnexion(){
        viewModelConnexion = ConnexionViewModel()

        viewModelConnexion.userLiveData.observe(viewLifecycleOwner, Observer {
            isBusy = false

            Toast.makeText(this.activity, it.data?.userId, Toast.LENGTH_SHORT).show()
        })

        viewModelConnexion.errorLiveData.observe(viewLifecycleOwner, Observer {
            isBusy = false

            val errorBodyApi = ErrorUtils.getErrorApi(it)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_SHORT).show() }
        })
    }

    /**
    Abonnement aux évènements de bouton de connexion et d'inscription
     */
    private fun setOnClickListener(){
        btn_login.setOnClickListener{
            if (checkFields()){
                val login = LoginDto(editText_email.text.toString(), editText_password.text.toString())
                if (!isBusy){
                    isBusy = true
                    viewModelConnexion.authenticate(login)
                }
            }
        }

        btn_register.setOnClickListener{
            Toast.makeText(activity, "Inscription OK", Toast.LENGTH_SHORT).show()
        }
    }

    /**
    Contrôle des différents champs que l'utilisateur doit renseigner
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