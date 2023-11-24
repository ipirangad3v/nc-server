package com.thondigital.nc.application.exception

class BlogNotFoundException(override val message: String) : Exception(message)

class BadRequestException(override val message: String) : Exception(message)

class UnauthorizedActivityException(override val message: String) : Exception(message)
