package com.example.optitrip.ui.fragments.connection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentLogInBinding
import com.example.optitrip.entities.Client
import com.example.optitrip.ui.DialogToken
import com.example.optitrip.ui.activities.MainActivity
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.SharedPreference
import com.example.optitrip.utils.forms.LogInForm
import com.example.optitrip.ui.viewmodels.ConnectionViewModel
import com.example.optitrip.ui.viewmodels.ConnectionViewModelFactory



/**
 * A simple [Fragment] subclass.
 *
 *  [LogInFragment] represents a login screen ui
 */
class LogInFragment : Fragment() {

    private lateinit var binding : FragmentLogInBinding
    private val viewModel: ConnectionViewModel by activityViewModels() { ConnectionViewModelFactory(requireContext()) }

    private lateinit var client : Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(SharedPreference.USER_USERNAME.key,"")
        if(  username!!.isNotEmpty() ) {
            //this.viewModel.checkClient(username)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        //reset to not initiate observer before the user entering infos
        viewModel.resetClient()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentLogInBinding.inflate(layoutInflater)
        this.initListeners()
        this.initObservers()
        // Inflate the layout for this fragment
        return binding.root
    }

    /**
     * Separate function to initiate listeners
     *
     */
    private fun initListeners() {
        this.binding.btnLoginLogin.setOnClickListener {
            Log.d("CVM","push button")
            val username = this.binding.etLoginUsername.text.toString()
            val password = this.binding.etLoginPassword.text.toString()

            val form = LogInForm(username,password)
            if( form.isValid) {
                Log.d("CVM","form valid")
                this.viewModel.checkClient(username)
            }else {
                Toast.makeText(requireContext(),"wrong format",Toast.LENGTH_SHORT).show()
            }
        }

        this.binding.btnLoginCancel.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_splashScreenFragment)
        }
    }

    /**
     * Function to initialize observers in order to react when the viewModel has finished its work
     *
     */
    private fun initObservers() {
        viewModel.resetClient()

        this.viewModel.client.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.ERROR) {
                Log.d("CVM",it.apiError.toString())
                this.binding.etLoginUsernameLayout.error =  getString(R.string.wrong_username)
                this.binding.etLoginPasswordLayout.error = null
            }else if(it.status == Resource.Status.SUCCESS) {
                loginCallback(it.data)
            }
        }

        this.viewModel.insertMsg.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.ERROR) Toast.makeText(requireContext(),getString(R.string.server_error_token),Toast.LENGTH_SHORT).show()
            else if(it.status == Resource.Status.SUCCESS) clientActivated()
        }

    }

    /**
     * Callback after checked if login information are correct.
     * Update ui if incorrect, go to main activity if correct
     * @param usernameValid boolean indicating username is correct
     * @param passwordValid boolean indicating password is correct
     */
    private fun loginCallback(client : Client?) {
        Log.d("CVM","Login callback")
        if(client==null) {
            this.binding.etLoginUsernameLayout.error =  getString(R.string.wrong_username)
            this.binding.etLoginPasswordLayout.error = null
        }else {
            val enteredPassword = this.binding.etLoginPassword.text.toString()
            if(enteredPassword != client.password) {
                this.binding.etLoginUsernameLayout.error = null
                this.binding.etLoginPasswordLayout.error = getString(R.string.wrong_password)
            }else {
                this.client = client
                if(client.pending ) {
                    Toast.makeText(requireContext(),getString(R.string.account_not_activated),Toast.LENGTH_SHORT).show()
                    DialogToken(requireContext()).createDialog(this::tokenValidated,client.token).show()
                }else {
                    this.login()
                }
            }
        }

    }

    /**
     * Callback from dialog entering token to activate account
     *
     */
    private fun tokenValidated() {
        viewModel.activateClient(this.client)
    }

    /**
     * Callback backend activate account
     */
    private fun clientActivated() {
        Toast.makeText(requireContext(),getString(R.string.token_ok),Toast.LENGTH_SHORT).show()
        this.login()
    }

    /**
     * Function intended for a user has entered correct information.
     * It saves its information and go to the [MainActivity]
     *
     */
    private fun login() {
        val  sharedPreferencesEditor = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferencesEditor.edit().putInt(SharedPreference.CLIENT_ID.key,client.clientId!!).apply()
        sharedPreferencesEditor.edit().putString(SharedPreference.USER_USERNAME.key,client.username).apply()

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }







}