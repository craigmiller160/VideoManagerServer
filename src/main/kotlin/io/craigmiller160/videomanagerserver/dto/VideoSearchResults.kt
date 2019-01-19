package io.craigmiller160.videomanagerserver.dto

data class VideoSearchResults (
        var totalFiles: Long = 0,
        var filesPerPage: Int = 0,
        var currentPage: Int = 0,
        var videoList: List<VideoFile> = ArrayList()
)