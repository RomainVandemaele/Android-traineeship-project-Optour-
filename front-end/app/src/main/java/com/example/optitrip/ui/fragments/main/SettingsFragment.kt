package com.example.optitrip.ui.fragments.main

import android.app.UiModeManager
import android.content.Context.UI_MODE_SERVICE
import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.optitrip.R
import com.example.optitrip.ui.activities.ConnectionActivity
import com.example.optitrip.ui.viewmodels.MainViewModel
import com.example.optitrip.ui.viewmodels.MainViewModelFactory
import com.example.optitrip.utils.SharedPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import java.io.InputStream

/**
 * [Fragment] for settings via [SharedPreference]
 *
 */
class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel : MainViewModel by activityViewModels() { MainViewModelFactory(requireContext())  }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        initListener()
    }


    private fun initListener() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPref.getString(SharedPreference.USER_USERNAME.toString(),"")
        val id = sharedPref.getInt(SharedPreference.CLIENT_ID.key,0)

        val listener = OnSharedPreferenceChangeListener { pref,key ->
            Log.e("SHARED PREF","LISTENED key $key")
            when(key) {
                SharedPreference.USER_USERNAME.key -> {
                    //change username api
                    val newUsername = pref.getString(key,"")
                    if( id!= 0 && newUsername!!.isNotEmpty() ) {
                        viewModel.changeUsername(id,newUsername)
                    }

                }

                getString(R.string.password_key) -> {
                    val newPassword = pref.getString(key,"")
                    if(id!=0 && newPassword!!.isNotEmpty()) {
                        if(newPassword.length <6) {
                            Toast.makeText(requireContext(),getString(R.string.password_too_short),Toast.LENGTH_LONG).show()
                        }else {
                            println(newPassword)
                            viewModel.changePassword(id,newPassword)
                            Toast.makeText(requireContext(),getString(R.string.new_password),Toast.LENGTH_LONG).show()
                        }

                    }
                }

                getString(R.string.dark_mode) -> {
                    val modeDark = pref.getBoolean(key,false)
                    if(modeDark) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                else -> {}
            }

        }

        val passwordPreference: EditTextPreference? = findPreference(getString(R.string.password_key))
        passwordPreference?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        }



        val prefLogOut = findPreference<Preference>(getString(R.string.log_out))
        prefLogOut?.setOnPreferenceClickListener {
            sharedPref.edit().remove(SharedPreference.USER_USERNAME.key).commit()
            sharedPref.edit().remove(SharedPreference.CLIENT_ID.key).commit()
            startActivity(Intent(requireActivity(),ConnectionActivity::class.java))
            true
        }

        val prefDelete = findPreference<Preference>(getString(R.string.delete_account))
        prefDelete?.setOnPreferenceClickListener {


            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_account))
                .setMessage(getString(R.string.really_delete_account))
                .setPositiveButton(getString(R.string.validate)) { _,_ ->
                    if(id!=0) {
                        viewModel.deleteClient(id)
                        sharedPref.edit().clear().commit()
                        startActivity(Intent(requireActivity(),ConnectionActivity::class.java))
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { _,_ -> }
                .create()
                .show()
            true
        }

        sharedPref.registerOnSharedPreferenceChangeListener(listener)
    }


}