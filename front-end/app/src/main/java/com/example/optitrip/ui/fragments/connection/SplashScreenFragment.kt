package com.example.optitrip.ui.fragments.connection

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentSplashScreenBinding
import com.example.optitrip.ui.activities.MainActivity
import com.example.optitrip.ui.viewmodels.ConnectionViewModel
import com.example.optitrip.ui.viewmodels.ConnectionViewModelFactory
import com.example.optitrip.utils.SharedPreference


/**
 * A simple [Fragment] subclass.
 * Use the [SplashScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 *  [SplashScreenFragment] represents a splash screen used to login/register
 *
 *  @property binding
 */
class SplashScreenFragment : Fragment() {

    private lateinit var binding : FragmentSplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        this.initListeners()

        if( PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt(SharedPreference.CLIENT_ID.key,0) !=0 ) {
           this.binding.btnSplashLogin.performClick()
        }

        return this.binding.root
    }


    /**
     * initialize listeners to [SplashScreenFragment] buttons for navigation
     *
     */
    private fun initListeners() {
        this.binding.btnSplashRegister.setOnClickListener {
            findNavController().navigate(R.id.action_splashScreenFragment_to_registerFragment)
        }

        this.binding.btnSplashLogin.setOnClickListener {
            findNavController().navigate(R.id.action_splashScreenFragment_to_logInFragment)
        }
    }




}