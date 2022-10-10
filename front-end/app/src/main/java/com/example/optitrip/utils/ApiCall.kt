package com.example.optitrip.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Class allowing to encapsulates an API call by doing the call and handling via liveData and lambda
 *
 * @param T
 * @property call  the [Call] of a retrofit request
 * @property resultLD the [MutableLiveData] that will contains the data and/or error message as a [Resource]
 */
class ApiCall<T> {

    lateinit var call: Call<T?>
    var resultLD: MutableLiveData<Resource<T>>? = null

    var postProcessing : ( (data : T?) -> Unit ) ? = null

    fun makeCall(call:Call<T?>, result: MutableLiveData<Resource<T>>?)  {
        this.call = call
        this.resultLD = result

        val callBackKt = CallBackKt()
        resultLD?.value = Resource.loading(null)
        this.call.enqueue(callBackKt)
    }


    inner class CallBackKt : Callback<T?> {

        override fun onFailure(call: Call<T?>, t: Throwable) {
            Log.d("API CALL","on failure : ${t.message}")
            resultLD?.value = Resource.error(t.message)
        }

        override fun onResponse(call: Call<T?>, response: Response<T?>) {

            Log.d("API CALL","on response")
            if(response.isSuccessful) {
                resultLD?.value = Resource.success(response.body())
                Log.d("API CALL","success")
                postProcessing?.invoke(response.body())
            }else{
                Log.d("API CALL","response not success ${response.message().toString()}")
                resultLD?.value = Resource.error(response.message().toString())
            }
        }
    }

}


