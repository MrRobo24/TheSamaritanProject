package com.identity_crisis.thesamaritans.model

class UserList(val uid: String, val username: String, val email: String, val profileImageUrl: String) {
    constructor() : this("","","", "")
}