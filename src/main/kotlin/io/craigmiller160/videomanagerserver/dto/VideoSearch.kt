package io.craigmiller160.videomanagerserver.dto

data class VideoSearch (
        var searchText: String?,
        var seriesId: Long?,
        var starId: Long?,
        var categoryId: Long?
)