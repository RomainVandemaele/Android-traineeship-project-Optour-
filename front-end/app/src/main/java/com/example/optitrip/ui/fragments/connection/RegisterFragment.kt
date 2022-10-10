package com.example.optitrip.ui.fragments.connection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.optitrip.R
import com.example.optitrip.databinding.FragmentRegisterBinding
import com.example.optitrip.entities.Client
import com.example.optitrip.ui.DialogToken
import com.example.optitrip.utils.Resource
import com.example.optitrip.utils.forms.RegisterForm
import com.example.optitrip.ui.viewmodels.ConnectionViewModel
import com.example.optitrip.ui.viewmodels.ConnectionViewModelFactory


/**
 * A simple [Fragment] subclass.
 * [RegisterFragment] represents a register screen ui
 *
 *
 */
class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private val viewModel: ConnectionViewModel by activityViewModels() { ConnectionViewModelFactory(requireContext()) }
    private var client : Client? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.binding = FragmentRegisterBinding.inflate(layoutInflater)
        this.initListeners()
        initObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetClient()
    }

    private fun initObservers() {
        viewModel.resetClient()

        this.viewModel.client.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.ERROR) registerCallback(null)
            else if(it.status == Resource.Status.SUCCESS) registerCallback(it.data)
        }

        this.viewModel.insertMsg.observe(viewLifecycleOwner) {
            if(it.status == Resource.Status.ERROR) clientActivated(false)
            else if(it.status == Resource.Status.SUCCESS) clientActivated(true)
        }

    }

    private fun initListeners() {
        this.binding.btnRegisterRegister.setOnClickListener {
            handleRegister()
        }

        this.binding.btnRegisterCancel.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_splashScreenFragment)
        }
    }

    /**
     * Handle regiser by validing info and creating user if valid
     *
     */
    private fun handleRegister() {
        val form = RegisterForm(
            this.binding.etRegisterFirstName.text.toString(),
            this.binding.etRegisterLastName.text.toString(),
            this.binding.etRegisterMail.text.toString(),
            this.binding.etRegisterUsername.text.toString(),
            this.binding.etRegisterPassword.text.toString()
        )
        if(form.isValid) {
            this.viewModel.addClient(form.toClient())
        }else {
            Toast.makeText(requireContext(),getString(R.string.register_wrong),Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Callback after a client was registered(or not) in the database
     *
     * @param client the new client if registered
     */
    private fun registerCallback( client: Client? = null ) {
        Log.e("register callback","callback $client")
        this.binding.etRegisterUsernameLayout.error = null
        if(client!=null) {
            this.client = client
            Toast.makeText(requireContext(),getString(R.string.welcome_p1) + " ${client.username}\n" + getString(R.string.welcome_p2),Toast.LENGTH_SHORT).show()
            DialogToken(requireContext()).createDialog(this::tokenValidated,this.client!!.token).show()

        }else {
            val errorMessage = viewModel.client.value?.apiError
            if (errorMessage != null) {
                Log.e("error message",errorMessage)
                when(errorMessage) {
                    "Bad Request" -> this.binding.etRegisterUsernameLayout.error = getString(R.string.register_username_taken)
                    "Failed to connect to /10.0.2.2:3000" -> Toast.makeText(requireContext(),getString(R.string.register_server_unavailable),Toast.LENGTH_SHORT).show()
                    else ->  Toast.makeText(requireContext(),getString(R.string.register_unknown_error),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    /**
     * Callback from dialog entering token to activate account
     */
    private fun tokenValidated() {
        viewModel.activateClient(this.client!!)
    }

    /**
     * Callback backend activate account
     * @param activated if activated done correctly
     */
    private fun clientActivated(activated : Boolean) {
        if(activated) Toast.makeText(requireContext(),getString(R.string.token_ok),Toast.LENGTH_SHORT).show()
        else Toast.makeText(requireContext(),getString(R.string.server_error_token),Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_registerFragment_to_splashScreenFragment)
    }


}