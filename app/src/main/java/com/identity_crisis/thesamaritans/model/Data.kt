package com.identity_crisis.thesamaritans.model

import android.net.Uri

class Data(
    val info1: String,
    val likes: Int,
    val name: String,
    val photo: String,
    val profileImageUrl: String,
    val saves: Int,
    val shares: Int,
    val timestmap: Long,
    val uid: String,
    val username: String
) {
    constructor() : this("", 0, "","a","a",0,0,0,"","")
}