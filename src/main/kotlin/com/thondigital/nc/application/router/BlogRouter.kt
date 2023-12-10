package com.thondigital.nc.application.router

import com.thondigital.nc.application.auth.principal.UserPrincipal
import com.thondigital.nc.application.controller.blog.BlogController
import com.thondigital.nc.application.controller.blog.BlogController.Companion.ONESIGNAL_APP_ID
import com.thondigital.nc.application.model.request.BlogRequest
import com.thondigital.nc.application.model.request.Notification
import com.thondigital.nc.application.model.request.NotificationMessage
import com.thondigital.nc.application.model.response.generateHttpResponse
import io.ktor.client.HttpClient
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.blogApi(
    httpClient: HttpClient,
    apiKey: String,
) {
    val blogController by inject<BlogController>()

    authenticate("jwt") {
        route("/blog") {
            get("/list") {
                val getBlogsRequest = call.request.queryParameters
                val getBlogsResults = blogController.getBlogsByQuery(getBlogsRequest, this.context)
                val response = generateHttpResponse(getBlogsResults)
                call.respond(response.code, response.body)
            }

            post("/notification") {
                val username = call.principal<UserPrincipal>()?.user?.username
                val notificationResponse =
                    blogController.sendNotification(
                        httpClient,
                        apiKey,
                        Notification(
                            includedSegments = listOf("All"),
                            headings = NotificationMessage(en = "Blogfy"),
                            contents = NotificationMessage(en = "$username has published a new blog\uD83D\uDD25"),
                            appId = ONESIGNAL_APP_ID,
                        ),
                    )
                val response = generateHttpResponse(notificationResponse)
                call.respond(response.code, response.body)
            }

            post {
                val createBlogRequest = call.receive<BlogRequest>()
                val createBlogResponse = blogController.storeBlog(createBlogRequest, this.context)
                val response = generateHttpResponse(createBlogResponse)
                call.respond(response.code, response.body)
            }

            put("/{blogId}") {
                val updateBlogRequest = call.receive<BlogRequest>()
                val updateBlogResponse = blogController.updateBlog(updateBlogRequest, this.context)
                val response = generateHttpResponse(updateBlogResponse)
                call.respond(response.code, response.body)
            }

            delete("/{blogId}") {
                val deleteBlogResponse = blogController.deleteBlog(this.context)
                val response = generateHttpResponse(deleteBlogResponse)
                call.respond(response.code, response.body)
            }

            get("{blogId}/is_author") {
                val checkAuthorResponse = blogController.checkBlogAuthor(this.context)
                val response = generateHttpResponse(checkAuthorResponse)
                call.respond(response.code, response.body)
            }
        }
    }
}
