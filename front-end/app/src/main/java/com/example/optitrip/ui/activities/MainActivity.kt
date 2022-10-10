package com.example.optitrip.ui.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.optitrip.R
import com.example.optitrip.databinding.ActivityMainBinding
import com.example.optitrip.ui.fragments.main.*
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory
import com.google.android.gms.maps.model.Marker


/**
 * [MainActivity] is the principal activity with a navigation bar that change fragment
 *
 */
class MainActivity :
    AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels { MainViewModelFactory(this@MainActivity) }
    private val map : MapsFragment = MapsFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        val navController = this.findNavController(R.id.fcv_main_fragment)
        val navBar = this.binding.tnvMainNavBar

        navBar.setupWithNavController(navController)

    }




}