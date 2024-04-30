package com.example.mangareader.model

import java.io.Serializable

class Genre : Serializable {
    var name: String? = null
    var url: String? = null
    override fun toString(): String {
        return name.toString()
    }
}