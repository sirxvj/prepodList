package com.example.prepod_list.data.repository

import com.example.prepod_list.data.api.RetrofitInstance
import com.example.prepod_list.model.Prepod
import retrofit2.Response

class Repository {
    suspend fun getPrepod(): Response<Prepod> {
        return RetrofitInstance.api.getPrepod()
    }

}