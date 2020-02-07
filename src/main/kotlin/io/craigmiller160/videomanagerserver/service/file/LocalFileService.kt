package io.craigmiller160.videomanagerserver.service.file

import io.craigmiller160.videomanagerserver.dto.LocalFileResponse
import io.craigmiller160.videomanagerserver.dto.LocalFileList
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

@Service
class LocalFileService (
         @Value("\${localfile.homeDir}")
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
                ?.map { file -> LocalFileResponse(file) }
                ?.sortedBy { localFile -> localFile.fileName }
                ?: listOf()
        val parentPath = File(dirPath).parentFile?.absolutePath ?: ""
        return LocalFileList(
                rootPath = dirPath,
                parentPath = parentPath,
                files = files
        )
    }

}
