package com.example.myapplication.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.io.Serializable


@Keep
data class TextResponse(
    @SerializedName("data")
    val data: TextDataResponse?,
    @SerializedName("data_error")
    val errMsg : String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?,
    @SerializedName("statusCode")
    val statusCode: Int?
) : Serializable

@Keep
data class TextDataResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("instagram")
    val instagram: String?,
    @SerializedName("job_title")
    val jobTitle: String?,
    @SerializedName("linkedin")
    val linkedin: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("website")
    val website: String?
): Serializable
