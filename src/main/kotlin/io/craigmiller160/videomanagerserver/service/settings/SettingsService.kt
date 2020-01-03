package io.craigmiller160.videomanagerserver.service.settings

import io.craigmiller160.videomanagerserver.dto.SETTINGS_ID
import io.craigmiller160.videomanagerserver.dto.Settings
import io.craigmiller160.videomanagerserver.repository.SettingsRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class SettingsService (private val settingsRepository: SettingsRepository) {

    @Transactional
    fun getOrCreateSettings(): Settings {
        return settingsRepository.findById(SETTINGS_ID)
                .orElseGet {
                    val newSettings = Settings(settingsId = SETTINGS_ID)
                    settingsRepository.save(newSettings)
                    newSettings
                }
    }

}