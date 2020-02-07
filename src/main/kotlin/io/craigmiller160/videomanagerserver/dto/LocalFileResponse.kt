package io.craigmiller160.videomanagerserver.dto

import java.io.File

data class LocalFileResponse (
        var fileName: String,
        var filePath: String,
        var isDirectory: Boolean
) {
    constructor(file: File) : this(
            file.name,
            file.absolutePath,
            file.isDirectory
    )
}
