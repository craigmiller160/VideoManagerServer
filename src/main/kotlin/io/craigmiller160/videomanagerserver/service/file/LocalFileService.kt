package io.craigmiller160.videomanagerserver.service.file

import io.craigmiller160.videomanagerserver.dto.LocalFile
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class LocalFileService (
         @Value("\${user.home}")
        private val userHome: String
) {

    fun getFilesFromDirectory(path: String?): List<LocalFile> {
        val dirPath = path ?: userHome
        return File(dirPath)
                .listFiles { file -> !file.isHidden }
                ?.map { file -> LocalFile(file) }
                ?: listOf()
    }

}