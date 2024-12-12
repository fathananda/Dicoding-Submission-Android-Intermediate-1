package com.example.middleprojectinter.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.middleprojectinter.utils.Event
import com.example.middleprojectinter.api.ApiConfig
import com.example.middleprojectinter.data.GetDetailStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailStoryViewModel"
    }

    private val _detailStory = MutableLiveData<StoryItems>()
    val detailStory: LiveData<StoryItems> = _detailStory

    private val _loadingScreen = MutableLiveData<Boolean>()
    val loadingScreen: LiveData<Boolean> = _loadingScreen

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    fun getDetailStory(userId: String) {
        _loadingScreen.value = true

        val cilent = ApiConfig.getApiService().getDetailStory(userId)
        cilent.enqueue(object : Callback<GetDetailStoryResponse> {
            override fun onResponse(
                call: Call<GetDetailStoryResponse>,
                response: Response<GetDetailStoryResponse>
            ) {
                _loadingScreen.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _detailStory.value = responseBody.story ?: StoryItems()
                        Log.d(TAG, responseBody.message.toString())
                    }
                } else {
                    _snackBarText.value = Event(response.message())
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetDetailStoryResponse>, t: Throwable) {
                _loadingScreen.value = false
                _snackBarText.value = Event("onFailure: Gagal, ${t.message ?: ""}")
                Log.e(TAG, "onFailure: Gagal")
            }
        })
    }
}