package io.craigmiller160.videomanagerserver.service.settings

import io.craigmiller160.modelmapper.EnhancedModelMapper
import io.craigmiller160.videomanagerserver.dto.SettingsPayload
import io.craigmiller160.videomanagerserver.entity.SETTINGS_ID
import io.craigmiller160.videomanagerserver.entity.Settings
import io.craigmiller160.videomanagerserver.repository.SettingsRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class SettingsService (
        private val settingsRepository: SettingsRepository,
        private val modelMapper: EnhancedModelMapper
) {

    @Transactional
    fun getOrCreateSettings(): SettingsPayload {
        val settings = settingsRepository.findById(SETTINGS_ID)
                .orElseGet {
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
