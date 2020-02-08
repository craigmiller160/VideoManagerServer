package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.entity.Settings
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/settings")
class SettingsController (private val settingsService: SettingsService) {

    @Secured(ROLE_ADMIN)
    @GetMapping
    fun getSettings(): ResponseEntity<Settings> {
        val settings = settingsService.getOrCreateSettings()
        return ResponseEntity.ok(settings)
    }

    @Secured(ROLE_ADMIN)
    @PutMapping
    fun updateSettings(@RequestBody settings: Settings): ResponseEntity<Settings> {
        val updatedSettings = settingsService.updateSettings(settings)
        return ResponseEntity.ok(updatedSettings)
    }

}