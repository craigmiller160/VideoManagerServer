/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.SettingsPayload
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
class SettingsController(private val settingsService: SettingsService) {

  @Secured(ROLE_ADMIN)
  @GetMapping
  fun getSettings(): ResponseEntity<SettingsPayload> {
    val settings = settingsService.getOrCreateSettings()
    return ResponseEntity.ok(settings)
  }

  @Secured(ROLE_ADMIN)
  @PutMapping
  fun updateSettings(@RequestBody settings: SettingsPayload): ResponseEntity<SettingsPayload> {
    val updatedSettings = settingsService.updateSettings(settings)
    return ResponseEntity.ok(updatedSettings)
  }
}
