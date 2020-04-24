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
import com.example.selfbuy.data.entity.local.Login
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.TokenDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.BaseActivity
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.home.viewModels.ConnexionViewModel
import com.example.selfbuy.presentation.home.viewModels.UserViewModel
import com.example.selfbuy.presentation.order.activity.SelectCreditCardActivity
import com.example.selfbuy.presentation.order.fragments.SelectCreditCardFragment
import com.example.selfbuy.presentation.profile.fragments.ProfileFragment
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

        if(this.activity is BaseActivity){
            val baseActivity = activity as BaseActivity
            baseActivity.supportActionBar(false)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val tokenSaved = SFApplication.app.loginPreferences.getString("token", "")
        val refreshTokenSaved = SFApplication.app.loginPreferences.getString("refreshToken", "")

        if (!tokenSaved.isNullOrEmpty() && !refreshTokenSaved.isNullOrEmpty()){
            progressBar_connexion.visibility = View.VISIBLE

            val token = TokenDto(tokenSaved.toString(), refreshTokenSaved.toString())
            CurrentUser.tokenDto = token
            userViewModel.getCurrentUser()
        }
    }

    /**
     * On s'abonne aux differents evenements de UserViewModel
     */
    private fun bindUserViewModel(){
        userViewModel = UserViewModel()

        userViewModel.userDtoLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<UserDto> ->
            progressBar_connexion.visibility = View.GONE
            val user = resultDto.data

            //Passer l'utilisateur a l'activity de profile et charger le fragment profile a la place de cette snackbar
            if (user != null){
                CurrentUser.userDto = user

                if(this.activity is SelectCreditCardActivity){
                    this.activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.select_credit_card_activity_fragment_container, SelectCreditCardFragment())
                        ?.commit()
                }
                else{
                    this.activity?.supportFragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.home_activity_fragment_container, ProfileFragment(user))
                        ?.commit()
                }

            }
        })

        userViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
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

        connexionViewModel.tokenDtoLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<TokenDto> ->
            SFApplication.app.loginPrefsEditor.putString("token", resultDto.data?.token)
            SFApplication.app.loginPrefsEditor.putString("refreshToken", resultDto.data?.refreshToken)
            SFApplication.app.loginPrefsEditor.commit()

            val token = TokenDto(resultDto.data!!.token, resultDto.data.refreshToken)
            CurrentUser.tokenDto = token

            userViewModel.getCurrentUser()
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
                val login = Login(editText_email.text.toString(), editText_password.text.toString())
                progressBar_connexion.visibility = View.VISIBLE
                connexionViewModel.authenticate(login)
            }
        }

        btn_register.setOnClickListener{
            if(this.activity is SelectCreditCardActivity){
                this.activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.select_credit_card_activity_fragment_container,InscriptionFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }
            else{
                this.activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(R.id.home_activity_fragment_container,InscriptionFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            }
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