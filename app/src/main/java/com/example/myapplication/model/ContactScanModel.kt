package com.example.myapplication.model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
data class ContactScanModel (
    val uri: String? = null,
    val listData : Map<String?,String?>? = null,
    val isNewData : Boolean? = null,
    val id : Int? = null
): Serializable