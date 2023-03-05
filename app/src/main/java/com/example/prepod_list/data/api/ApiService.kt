package com.example.prepod_list.data.api

import com.example.prepod_list.model.Prepod
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("v1/employees/all")
    suspend fun getPrepod(): Response<Prepod>
}