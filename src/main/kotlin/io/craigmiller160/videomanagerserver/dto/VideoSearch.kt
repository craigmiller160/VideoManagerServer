package io.craigmiller160.videomanagerserver.dto

data class VideoSearch (
        var searchText: String? = null,
        var seriesId: Long? = null,
        var starId: Long? = null,
        var categoryId: Long? = null
)