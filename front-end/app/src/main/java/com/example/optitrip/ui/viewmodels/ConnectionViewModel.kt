package com.example.optitrip.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.optitrip.entities.Client
import com.example.optitrip.retrofit.ClientAPI
import com.example.optitrip.retrofit.RetrofitClient
import com.example.optitrip.room.AppDatabase
import com.example.optitrip.room.daos.UserDao
import com.example.optitrip.room.entities.User
import com.example.optitrip.utils.ApiCall
import com.example.optitrip.utils.Resource
import kotlinx.coroutines.launch

/**
 * View model attached to [ConnectionActivity] and its fragment.
 *
 * @property context the context for access to DB
 * @property api the api to to request to the Client API
 * @property _client liveData storing the user connected/registered when the form is valid
 * @property client accessible non mutable livedata linked to [_client] for the purpose of being observed by the fragments
 *
 */
class ConnectionViewModel(private val context: Context) : ViewModel() {

    private val api : ClientAPI = RetrofitClient.clientLocal.create(ClientAPI::class.java)
    private val userDao : UserDao = AppDatabase.instance(context).UserDao()


    private val _client : MutableLiveData<Resource<Client>> = MutableLiveData<Resource<Client>>()
    val client : LiveData<Resource<Client>> = _client


    /**
     * _insert msg observable for string response of not GET request to REST API
     */
    private val _insertMsg : MutableLiveData<Resource<String>> = MutableLiveData<Resource<String>>()
    val insertMsg : LiveData<Resource<String>> = _insertMsg


    /**
     * Check if a user exists by calling the API and then calling a callback function to the fragment
     *
     * @param username username entered by the user
     * @param password password entered by the user
     * @param callback callback function to the fragment [LogInFragment]
     */
    fun checkClient(username: String) {
        Log.d("CVM","yop")
        val apiCall = ApiCall<Client>()
        apiCall.makeCall(api.getClientByUsername(username),_client)
        Log.d("CVM","end fct check user")
    }

    /**
     * Function putting default value into the livedata to ensure no unintended observer activation
     */
    fun resetClient() {
        _client.value = Resource.loading(null)
        _insertMsg.value = Resource.loading(null)
    }



    /**
     * Insert a new user in the database
     *
     * @param client the client to insert
     */
    fun addClient(client: Client) {
        val apiCall = ApiCall<Client>()
        apiCall.postProcessing = {
            viewModelScope.launch {
                if(it!= null) userDao.insert(User(it.clientId!!,it.username,it.password))
            }
        }
        apiCall.makeCall(api.postClient(client),_client)
    }

    /**
     * Activate a client by sendind request to backend to put its pending status to false
     *
     * @param client client to activate
     * @return true if activation done and false if there was an error
     */
    fun activateClient(client: Client)   { //callback : (Boolean) -> Unit
        val apiCall = ApiCall<String>()
        apiCall.makeCall(api.activateClient(client.clientId!!),_insertMsg)
    }

}