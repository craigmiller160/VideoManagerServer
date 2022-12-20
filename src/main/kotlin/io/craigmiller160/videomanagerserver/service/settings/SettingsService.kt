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

package io.craigmiller160.videomanagerserver.service.settings

import io.craigmiller160.videomanagerserver.dto.SettingsPayload
import io.craigmiller160.videomanagerserver.entity.SETTINGS_ID
import io.craigmiller160.videomanagerserver.entity.Settings
import io.craigmiller160.videomanagerserver.mapper.VMModelMapper
import io.craigmiller160.videomanagerserver.repository.SettingsRepository
import javax.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class SettingsService(
  private val settingsRepository: SettingsRepository,
  private val modelMapper: VMModelMapper
) {

  @Transactional
  fun getOrCreateSettings(): SettingsPayload {
    val settings =
      settingsRepository.findById(SETTINGS_ID).orElseGet {
        val newSettings = Settings(settingsId = SETTINGS_ID)
        settingsRepository.save(newSettings)
      }
    return modelMapper.map(settings, SettingsPayload::class.java)
  }

  @Transactional
  fun updateSettings(payload: SettingsPayload): SettingsPayload {
    val settings = modelMapper.map(payload, Settings::class.java)
    settings.settingsId = SETTINGS_ID
    settingsRepository.save(settings)
    return payload
  }
}
