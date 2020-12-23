package com.identity_crisis.thesamaritans.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Form (val title: String,val category: String, val type: String, val state: String, val city: String, val pincode: String, val description: String, val contact: String, val formStatus: String) :
    Parcelable {
    constructor() : this("","","","","","","","", "0")
}

