package io.craigmiller160.videomanagerserver.service.settings

import io.craigmiller160.videomanagerserver.repository.SettingsRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsServiceTest {

    @Mock
    private lateinit var settingsRepository: SettingsRepository

    @InjectMocks
    private lateinit var settingsService: SettingsService

    @Test
    fun test_getOrCreateSettings_get() {
        TODO("Finish this")
    }

    @Test
    fun test_getOrCreateSettings_create() {
        TODO("Finish this")
    }

}