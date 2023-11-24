package com.thondigital.nc.application.model.request

import kotlinx.serialization.Serializable

@Serializable
data class NotificationRequest(
    val title: String,
)
