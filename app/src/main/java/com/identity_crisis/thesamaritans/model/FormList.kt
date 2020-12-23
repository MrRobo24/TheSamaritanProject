package com.identity_crisis.thesamaritans.model

class FormList (val category: String,  val type: String, val state: String, val city: String, val pincode: String, val description: String, val contact: String) {
    constructor() : this("","","","","","","")
}