package com.identity_crisis.thesamaritans.model

class ChatMessage() {
    var id: String = ""
    var text: String = ""
    var fromId: String = ""
    var toId: String = ""
    var timeStamp: Long = 0

    constructor(
        id: String = "",
        text: String = "",
        fromId: String = "",
        toId: String = "",
        timeStamp: Long = 0
    ) : this() {
        this.id = id
        this.text = text
        this.fromId = fromId
        this.toId = toId
        this.timeStamp = timeStamp
    }
}