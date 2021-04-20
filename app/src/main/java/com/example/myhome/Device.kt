package com.example.myhome

data class Device(
    val id: String,
    var name: String,
    var room: String,
    var level: Int,
    var on: Boolean,
    val type: String
) {
    constructor(): this("","","",0,false,"")
    companion object {
        const val TYPE_FAN = "Fan"
        const val TYPE_LIGHT = "Light"
        const val TYPE_OTHER = "Other"
    }
}