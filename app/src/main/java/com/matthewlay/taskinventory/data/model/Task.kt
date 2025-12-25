package com.matthewlay.taskinventory.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Date

data class Task(
    // UBAH KE String? AGAR ANTI-CRASH

    // (Gson otomatis ubah angka jadi string kalau server kirim angka)
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("category")
    val category: String,

    @SerializedName("status")
    var status: String = "New",

    @SerializedName("created_time")
    val createdTime: String? = null,

    @SerializedName("finished_time")
    val finishedTime: String? = null,

    @SerializedName("duration")
    val duration: String? = null,

    var startTime: Date? = null,
    var endTime: Date? = null
) : Serializable