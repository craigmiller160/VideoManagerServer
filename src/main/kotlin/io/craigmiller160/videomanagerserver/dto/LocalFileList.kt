package io.craigmiller160.videomanagerserver.dto

// TODO refactor

data class LocalFileList (
        var rootPath: String = "",
        var parentPath: String = "",
        var files: List<LocalFile> = listOf()
)
