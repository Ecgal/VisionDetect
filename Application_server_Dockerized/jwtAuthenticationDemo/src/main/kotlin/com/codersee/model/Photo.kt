package com.codersee.model

import com.codersee.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Photo(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val filename: String,
    val owner: String,
    val createdAt: String,
)