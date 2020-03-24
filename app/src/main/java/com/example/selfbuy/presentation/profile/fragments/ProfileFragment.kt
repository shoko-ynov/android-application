package com.example.selfbuy.presentation.profile.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.local.CurrentUser
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.SFApplication
import com.example.selfbuy.presentation.home.fragments.ConnexionFragment
import com.example.selfbuy.presentation.home.viewModels.UserViewModel
import com.example.selfbuy.presentation.profile.activity.ProfileModifActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_modif.*

class ProfileFragment(private val userDto: UserDto) : Fragment() {

    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.profile_menu_edit){
            //On redirige vers la page de modif du profile en passant l'utilisateur en cours
            val intent = Intent(this.context, ProfileModifActivity::class.java)
            startActivity(intent)
        }
        else if(item.itemId == R.id.profile_menu_logout){
            //On supprime l'utilisateur en cours
            CurrentUser.tokenDto = null
            CurrentUser.userDto = null

            //On supprime les SharedPreferences (token et refreshToken)
            SFApplication.app.loginPrefsEditor.clear()
            SFApplication.app.loginPrefsEditor.commit()

            view?.let { v -> Snackbar.make(v, getString(R.string.you_are_disconnected), Snackbar.LENGTH_SHORT).show() }

            //Redirection page connexion
            this.activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.home_activity_fragment_container, ConnexionFragment())
                ?.commit()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.bindUserViewModel()

        refreshUser(userDto)
    }

    override fun onResume() {
        super.onResume()

        progressBar_profil_detail.visibility = View.VISIBLE
        userViewModel.getCurrentUser()
    }

    private fun refreshUser(user: UserDto){
        val name = "${user.firstName} ${user.lastName}"
        tw_name_profile_detail.text = name

        val address = "${user.address} - ${user.postalCode} ${user.city}"
        tw_address_profile_detail.text = address

        tw_email_profile_detail.text = user.mail
    }

    /**
     * On s'abonne aux differents evenements de UserViewModel
     */
    private fun bindUserViewModel(){
        userViewModel.userDtoLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<UserDto> ->
            progressBar_profil_detail.visibility = View.GONE
            refreshUser(resultDto.data!!)
        })

        userViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            //si probleme revenir sur le fragment connexion
            progressBar_profil_modif.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }
}
