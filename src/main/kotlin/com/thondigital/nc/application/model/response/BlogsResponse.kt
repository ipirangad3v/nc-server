package com.thondigital.nc.application.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BlogsResponse(
    override val status: State,
    override val message: String,
    val pagination: Pagination? = null,
    val results: List<BlogDomainModel> = emptyList(),
) : Response {
    companion object {
        fun failed(message: String) =
            BlogsResponse(
                State.FAILED,
                message,
            )

        fun success(
            pagination: Pagination?,
            blogs: List<BlogDomainModel>,
            message: String,
        ) = BlogsResponse(
            State.SUCCESS,
            message,
            pagination,
            blogs,
        )
    }
}

@Serializable
data class Pagination(
    val totalCount: Int,
    val currentPage: Int,
    val totalPages: Int,
    val links: Links,
)

@Serializable
data class Links(
    val previous: Int? = null,
    val next: Int? = null,
)
