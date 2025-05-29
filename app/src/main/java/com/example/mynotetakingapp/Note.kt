package com.example.mynotetakingapp

data class Note(
    val id: Int,
    var title: String,
    var text: String,
    var timestamp: Long
)
