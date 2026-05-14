package com.arthwh.ondeestacionei.model

import java.io.Serializable

data class Location (
    val id: Int = -1, //default
    var title: String,
    var description: String,
    var latitude: Double,
    var longitude: Double,
    var imagePath: String,
    val addedAt: String,
    val active: Boolean = true
) : Serializable