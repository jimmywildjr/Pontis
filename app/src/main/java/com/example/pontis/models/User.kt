package com.example.pontis.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val gender: String = "",
    val schoolYear: String = "",
    val homeCity: String = "",
    val profileCompleted: Int = 0, ):Parcelable
