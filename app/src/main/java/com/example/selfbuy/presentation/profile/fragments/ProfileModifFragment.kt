package com.example.selfbuy.presentation.profile.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.home.viewModels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_modif.*

class ProfileModifFragment : Fragment() {

    private var hasRefreshedOnError = false
    private lateinit var user: UserDto
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_modif_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.profile_modif_menu_save) {
            progressBar_profil_modif.visibility = View.VISIBLE

            val updatedUser = UserDto(user.isAdmin, user._id,
                editText_email_profile_modif.text.toString(),
                user.activationKey, user.active, user.registrationDate,
                editText_firstname_profile_modif.text.toString(),
                editText_lastname_profile_modif.text.toString(),
                editText_address_profile_modif.text.toString(),
                editText_city_profile_modif.text.toString(),
                editText_postalcode_profile_modif.text.toString())

            userViewModel.putUserById(updatedUser)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile_modif, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.bindUserViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        refreshUser()
    }

    /**
     * On s'abonne aux differents evenements de UserViewModel
     */
    private fun bindUserViewModel(){
        userViewModel = UserViewModel()

        userViewModel.userDtoLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<UserDto> ->
            progressBar_profil_modif.visibility = View.GONE
            user = resultDto.data!!

            editText_lastname_profile_modif.setText(user.lastName)
            editText_firstname_profile_modif.setText(user.firstName)

            editText_address_profile_modif.setText(user.address)
            editText_city_profile_modif.setText(user.city)
            editText_postalcode_profile_modif.setText(user.postalCode)

            editText_email_profile_modif.setText(user.mail)
        })

        userViewModel.refreshUserLiveDate.observe(viewLifecycleOwner, Observer {
            view?.let { v -> Snackbar.make(v, "Profile modifié avec succès.", Snackbar.LENGTH_LONG).show() }

            this.activity?.finish()
        })

        userViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            //si probleme revenir sur le fragment connexion
            progressBar_profil_modif.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }

            if (!hasRefreshedOnError){
                hasRefreshedOnError = true
                refreshUser()
            }
        })
    }

    private fun refreshUser(){
        progressBar_profil_modif.visibility = View.VISIBLE
        userViewModel.getCurrentUser()
    }
}
