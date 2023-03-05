package com.example.prepod_list.screens.mainView

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prepod_list.data.repository.Repository
import com.example.prepod_list.model.Prepod
import com.example.prepod_list.model.PrepodItem
import kotlinx.coroutines.launch
import retrofit2.Response

class StartViewModel: ViewModel() {
    val repo = Repository()
    val PrepodList: MutableLiveData<Response<Prepod>> = MutableLiveData()
    fun getPrepod(){
        viewModelScope.launch {
           PrepodList.value = repo.getPrepod()
        }
    }
}