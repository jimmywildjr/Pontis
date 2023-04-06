package com.example.pontis.models

import android.icu.text.CaseMap.Title
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FollowItem(
    val user_id: String="",
    val opportunity_id: String="",
    val title: String = "",
    val schoolYear: String = "",
    val homeCity: String = "",
    val industry: String = "",
    val type: String = "",
    val image: String = "",
    var id: String = "",
): Parcelable
