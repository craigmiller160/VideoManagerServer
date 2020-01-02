package io.craigmiller160.videomanagerserver.service.file

import io.craigmiller160.videomanagerserver.dto.LocalFile
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileService {

    fun getFilesFromDirectory(path: String?): List<LocalFile> {
        val dirPath = path ?: System.getProperty("user.home")
        return File(dirPath)
                .listFiles { file -> !file.isHidden }
                ?.map { file -> LocalFile(file) }
                ?: listOf()
    }

}
