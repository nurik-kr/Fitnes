package ru.nurik.fitnes.ui.onBoard

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataOnBoard (
    val image:Int,
    val textOne:String,
    val textTwo: String
): Parcelable