package com.example.selfbuy.presentation.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.selfbuy.R
import com.example.selfbuy.data.entity.remote.ProductDto
import com.example.selfbuy.data.entity.remote.ResultApiDto
import com.example.selfbuy.handleError.utils.ErrorUtils
import com.example.selfbuy.presentation.home.viewModels.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_connexion.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.bindHomeViewModel()
    }

    /**
     * On s'abonne aux differents evenements de HomeViewModel
     */
    private fun bindHomeViewModel(){
        homeViewModel = HomeViewModel()

        homeViewModel.productLiveData.observe(viewLifecycleOwner, Observer { resultDto: ResultApiDto<ArrayList<ProductDto>> ->
            //progressBar_connexion.visibility = View.GONE
            view?.let { v -> Snackbar.make(v, resultDto.data?.get(0)?.name.toString(), Snackbar.LENGTH_LONG).show() }
        })

        homeViewModel.errorLiveData.observe(viewLifecycleOwner, Observer { error: Throwable ->
            //progressBar_connexion.visibility = View.GONE

            val errorBodyApi = ErrorUtils.getErrorApi(error)
            view?.let { v -> Snackbar.make(v, errorBodyApi.message, Snackbar.LENGTH_LONG).show() }
        })
    }
}
