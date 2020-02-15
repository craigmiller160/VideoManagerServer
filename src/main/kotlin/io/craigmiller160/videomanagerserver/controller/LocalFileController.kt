package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.LocalFileListResponse
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.service.file.LocalFileService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/localfiles")
class LocalFileController(private val localFileService: LocalFileService) {

    @Secured(ROLE_ADMIN)
    @GetMapping("/directory")
    fun getFilesFromDirectory(
            @RequestParam(required = false) path: String?,
            @RequestParam(required = false, defaultValue = "false") onlyDirectories: Boolean
    ): ResponseEntity<LocalFileListResponse> {
        val actualPath = if (path == null || path.isEmpty()) null else path
        val fileList = localFileService.getFilesFromDirectory(actualPath, onlyDirectories)
        return ResponseEntity.ok(fileList)
    }

}
