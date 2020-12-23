package com.identity_crisis.thesamaritans.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profileImageUrl: String = ""
): Parcelable