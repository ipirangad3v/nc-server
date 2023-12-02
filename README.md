# nc-server

A Kotlin Multiplatform (KMP) API and server for [NC KMP aplication](https://github.com/ipirangad3v/nc-kmp), built using Ktor.

## Overview

this is a versatile API designed to support the NC application, which aims to provide a seamless and consistent experience across multiple platforms. This project utilizes Kotlin Multiplatform to share code between different platforms, allowing for efficient development and maintenance.

## Features

- **Ktor**: The API is built on top of Ktor, a powerful and flexible Kotlin framework for building asynchronous servers and clients.
- **Modular Structure**: The project is organized into modules, making it easy to manage and extend. The modular structure allows for a clear separation of concerns.
- **RESTful Endpoints**: The API exposes RESTful endpoints to handle various operations required by the NC application.
- **Database Integration**: nc-server integrates with a database to store and retrieve relevant information. The database schema and interactions are designed to meet the specific needs of the NC application.

## Dependencies

- **Ktor:** A framework for building asynchronous servers and clients in connected systems.
- **Kotlinx.serialization:** A Kotlin library for parsing JSON into Kotlin objects and serializing Kotlin objects into JSON.
- **Exposed:** A lightweight SQL library for Kotlin.
- **HikariCP:** A high-performance JDBC connection pool.
