package com.thondigital.nc.application.model.request

import kotlinx.serialization.Serializable

@Serializable
data class BlogRequest(
    val title: String,
    val description: String,
    val creationTime: String,
)
