package io.craigmiller160.videomanagerserver.service.file

import io.craigmiller160.videomanagerserver.dto.LocalFile
import io.craigmiller160.videomanagerserver.dto.LocalFileList
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class LocalFileService (
         @Value("\${user.home}")
        private val userHome: String
) {

    fun getFilesFromDirectory(path: String?, onlyDirectories: Boolean): LocalFileList {
        val dirPath = path ?: userHome
        val files = File(dirPath)
                .listFiles { file ->
                    if (onlyDirectories) {
                        return@listFiles !file.isHidden && file.isDirectory
                    }
                    !file.isHidden
                }
                ?.map { file -> LocalFile(file) }
                ?: listOf()
        return LocalFileList(
                rootPath = dirPath,
                files = files
        )
    }

}
