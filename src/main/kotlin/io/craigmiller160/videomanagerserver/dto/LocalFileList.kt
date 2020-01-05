package io.craigmiller160.videomanagerserver.dto

data class LocalFileList (
        var rootPath: String = "",
        var files: List<LocalFile> = listOf()
)
