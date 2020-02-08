package io.craigmiller160.videomanagerserver.service.settings

import com.nhaarman.mockito_kotlin.isA
import io.craigmiller160.videomanagerserver.dto.SettingsPayload
import io.craigmiller160.videomanagerserver.entity.SETTINGS_ID
import io.craigmiller160.videomanagerserver.entity.Settings
import io.craigmiller160.videomanagerserver.repository.SettingsRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.Optional
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class SettingsServiceTest {

    companion object {
        private const val ROOT_DIR = "rootDir"
    }

    @Mock
    private lateinit var settingsRepository: SettingsRepository

    @InjectMocks
    private lateinit var settingsService: SettingsService

    @Test
    fun test_getOrCreateSettings_get() {
        val settings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )
        `when`(settingsRepository.findById(SETTINGS_ID))
                .thenReturn(Optional.of(settings))

        val result = settingsService.getOrCreateSettings()
        assertThat(result, allOf(
                hasProperty("rootDir", equalTo(ROOT_DIR))
        ))

        verify(settingsRepository, times(0))
                .save(isA<Settings>())
    }

    @Test
    fun test_getOrCreateSettings_create() {
        val settings = Settings(
                settingsId = SETTINGS_ID
        )
        `when`(settingsRepository.findById(SETTINGS_ID))
                .thenReturn(Optional.empty())
        `when`(settingsRepository.save(settings))
                .thenReturn(settings)

        val result = settingsService.getOrCreateSettings()
        assertThat(result, allOf(
                hasProperty("rootDir", emptyString())
        ))

        verify(settingsRepository, times(1))
                .save(isA<Settings>())
    }

    @Test
    fun test_updateSettings() {
        val saveSettings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )
        val settingsPayload = SettingsPayload(
                rootDir = ROOT_DIR
        )
        `when`(settingsRepository.save(saveSettings))
                .thenReturn(saveSettings)

        val result = settingsService.updateSettings(settingsPayload)
        assertEquals(settingsPayload, result)
    }

}