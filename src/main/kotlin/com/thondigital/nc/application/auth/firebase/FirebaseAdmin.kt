package com.thondigital.nc.application.auth.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.ByteArrayInputStream
import java.util.Base64

object FirebaseAdmin {
    private val base64Config: String = System.getenv("GOOGLE_CONFIG_BASE64")
    private val credentials: GoogleCredentials = GoogleCredentials.fromStream(
        ByteArrayInputStream(Base64.getDecoder().decode(base64Config))
    )

    private val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(credentials)
        .build()

    fun init(): FirebaseApp = FirebaseApp.initializeApp(options)
}