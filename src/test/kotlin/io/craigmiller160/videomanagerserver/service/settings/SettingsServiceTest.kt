package io.craigmiller160.videomanagerserver.service.settings

import com.nhaarman.mockito_kotlin.isA
import io.craigmiller160.videomanagerserver.dto.SETTINGS_ID
import io.craigmiller160.videomanagerserver.dto.Settings
import io.craigmiller160.videomanagerserver.repository.SettingsRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.nullValue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
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
                hasProperty("settingsId", equalTo(SETTINGS_ID)),
                hasProperty("rootDir", equalTo(ROOT_DIR))
        ))

        verify(settingsRepository, times(0))
                .save(isA<Settings>())
    }

    @Test
    fun test_getOrCreateSettings_create() {
        `when`(settingsRepository.findById(SETTINGS_ID))
                .thenReturn(Optional.empty())

        val result = settingsService.getOrCreateSettings()
        assertThat(result, allOf(
                hasProperty("settingsId", equalTo(SETTINGS_ID)),
                hasProperty("rootDir", emptyString())
        ))

        verify(settingsRepository, times(1))
                .save(isA<Settings>())
    }

    @Test
    fun test_updateSettings() {
        val settingsArg = Settings(
                settingsId = 2L,
                rootDir = ROOT_DIR
        )
        val settingsResult = Settings(
                settingsId = SETTINGS_ID,
                rootDir = ROOT_DIR
        )
        `when`(settingsRepository.save(settingsResult))
                .thenReturn(settingsResult)

        val result = settingsService.updateSettings(settingsArg)
        assertEquals(settingsResult, result)
    }

}