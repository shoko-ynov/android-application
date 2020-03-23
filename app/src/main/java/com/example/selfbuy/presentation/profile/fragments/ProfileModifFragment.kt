package com.example.selfbuy.presentation.profile.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.data.entity.remote.UserDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.home.viewModels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile_modif.*

class ProfileModifFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

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

        progressBar_profil_modif.visibility = View.VISIBLE
        userViewModel.getCurrentUser()
    }

    /**
     * On s'abonne aux differents evenements de UserViewModel
     */
    private fun bindUserViewModel(){
        userViewModel = UserViewModel()

        userViewModel.userDtoLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<UserDto> ->
            progressBar_profil_modif.visibility = View.GONE
            val user = resultDto.data

            if (user != null){
                val name = "${user.firstName} ${user.lastName}"
                editText_name_profile_modif.setText(name)

                val address = "${user.address} - ${user.postalCode} ${user.city}"
                editText_address_profile_modif.setText(address)

                editText_email_profile_modif.setText(user.mail)
            }
        })

        userViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            //si probleme revenir sur le fragment connexion
            progressBar_profil_modif.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }
}
