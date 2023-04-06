package com.example.pontis.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Opportunity(
    val user_id: String = "",
    val user_name: String = "",
    val title: String = "",
    val description: String = "",
    val link: String = "",
    val schoolYear: String = "",
    val homeCity: String = "",
    val industry: String = "",
    val type: String = "",
    val image: String = "",
    var opportunity_id: String = "",
):Parcelable
