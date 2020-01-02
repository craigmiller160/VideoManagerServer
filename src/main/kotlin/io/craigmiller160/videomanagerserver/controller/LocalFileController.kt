package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.LocalFile
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.service.file.LocalFileService
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/files")
class LocalFileController(private val localFileService: LocalFileService) {

    @Secured(ROLE_ADMIN)
    @GetMapping("/directory")
    fun getFilesFromDirectory(path: String?): List<LocalFile> {
        TODO("Finish this")
    }

    @GetMapping("/print")
    fun printValue() {
        localFileService.printValue()
    }

}
