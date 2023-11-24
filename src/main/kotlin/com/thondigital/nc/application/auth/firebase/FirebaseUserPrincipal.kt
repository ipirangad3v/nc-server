package com.thondigital.nc.application.auth.firebase

import io.ktor.auth.Principal

class FirebaseUserPrincipal(val email: String) : Principal
