package io.craigmiller160.videomanagerserver.dto

data class LocalFileListResponse (
        var rootPath: String = "",
        var parentPath: String = "",
        var files: List<LocalFileResponse> = listOf()
)
