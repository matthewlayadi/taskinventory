package com.matthewlay.taskinventory.data.remote

import com.matthewlay.taskinventory.data.model.Task
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // 1. Mengambil semua data task untuk ditampilkan di List [cite: 56]
    @GET("tasks")
    fun getAllTasks(): Call<List<Task>>

    // 2. Membuat task baru (Create) [cite: 12]
    @POST("tasks")
    fun createTask(@Body task: Task): Call<Task>

    // 3. Update status (misal dari "New" -> "In Progress") [cite: 16]
    // Kita kirim ID task di URL, dan data status baru di Body
    @PUT("tasks/{id}")
    fun updateTaskStatus(
        @Path("id") id: String,
        @Body task: Task
    ): Call<Task>

    // 4. (Opsional) Hapus task jika diperlukan nanti
    @DELETE("tasks/{id}")
    fun deleteTask(@Path("id") id: String): Call<Void>
}