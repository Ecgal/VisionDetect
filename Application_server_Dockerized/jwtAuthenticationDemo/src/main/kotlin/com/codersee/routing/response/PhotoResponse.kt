package com.codersee.routing.response

import kotlinx.serialization.Serializable

@Serializable
data class PhotoResponse(
    val filename: String,
    val path: String,
    val size: Long
)