package io.craigmiller160.videomanagerserver.service.settings

import io.craigmiller160.videomanagerserver.entity.SETTINGS_ID
import io.craigmiller160.videomanagerserver.entity.Settings
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

    @Transactional
    fun updateSettings(settings: Settings): Settings {
        settings.settingsId = SETTINGS_ID
        return settingsRepository.save(settings)
    }

}