package com.example.middleprojectinter.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.middleprojectinter.api.ApiConfig
import com.example.middleprojectinter.response.ErrorResponse
import com.example.middleprojectinter.response.LoginResponse
import com.example.middleprojectinter.view.main.PayloadLogin
import com.example.middleprojectinter.data.pref.UserModel
import com.example.middleprojectinter.data.pref.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _login = MutableLiveData<Boolean>()
    val login: LiveData<Boolean> = _login

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private fun saveUser(user: UserModel) {
        viewModelScope.launch {
            ApiConfig.setToken(user.tokenAuth)
            pref.saveUser(user)
        }
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun login(email: String, password: String) {
        val payload = PayloadLogin(email, password)
        val client = ApiConfig.getApiService().login(payload)
        _isLoading.value = true
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val token = responseBody.loginResult?.token as String
                        _login.value = true
                        saveUser(UserModel(token, true))
                        _snackbarText.value = responseBody.message
                    }
                } else {
                    val responseBody = response.errorBody()
                    _login.value = false
                    if (responseBody != null) {
                        val mapper =
                            Gson().fromJson(responseBody.string(), ErrorResponse::class.java)
                        _snackbarText.value = mapper.message
                        Log.e(TAG, "onFailure2: ${mapper.message}")
                    } else {
                        _snackbarText.value = response.message()
                        Log.e(TAG, "onFailure2: ${response.message()}")
                    }

                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _login.value = false
                _snackbarText.value = t.message ?: "Error !"
                Log.e(TAG, "onFailure: Failed, ${t.message}")
            }
        })
    }
}