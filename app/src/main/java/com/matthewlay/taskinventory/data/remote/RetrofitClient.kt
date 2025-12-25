package com.matthewlay.taskinventory.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Ganti IP ini nanti sesuai IP Laptop/Komputer tempat server berjalan
    // 10.0.2.2 adalah localhost khusus untuk Emulator Android
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}