package io.craigmiller160.videomanagerserver.dto

import java.io.File

data class LocalFile (
        val fileName: String,
        val filePath: String,
        val isDirectory: Boolean
) {
    constructor(file: File) : this(
            file.name,
            file.absolutePath,
            file.isDirectory
    )
}
