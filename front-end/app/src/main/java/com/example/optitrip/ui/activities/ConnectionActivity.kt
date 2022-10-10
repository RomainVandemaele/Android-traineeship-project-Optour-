package com.example.optitrip.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.optitrip.R
import com.example.optitrip.databinding.ActivityConnectionBinding
import com.example.optitrip.ui.fragments.connection.LogInFragment
import com.example.optitrip.ui.fragments.connection.RegisterFragment
import com.example.optitrip.ui.fragments.connection.SplashScreenFragment
import com.example.optitrip.ui.viewmodels.ConnectionViewModel


/**
 * Class that represent the first activity which encapsulate the whole connection ui
 * This activity has 3 fragments :
 *  -Splash screen with login and register buttons
 *  -Login fragment
 *  -Register fragment
 *
 *  @property binding it links the activity to its layout
 *  @property viewModel it liks the ui activity and the business logic of connection
 */
class ConnectionActivity :
    AppCompatActivity()
{

    private lateinit var binding : ActivityConnectionBinding
    private val viewModel : ConnectionViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityConnectionBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
    }


}