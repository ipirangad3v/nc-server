package com.thondigital.nc.application.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RevokeTokenRequest(
    val token: String,
)
