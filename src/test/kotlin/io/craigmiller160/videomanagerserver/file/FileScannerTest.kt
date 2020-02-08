package io.craigmiller160.videomanagerserver.file

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.entity.SETTINGS_ID
import io.craigmiller160.videomanagerserver.entity.Settings
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.exception.InvalidSettingException
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean

class FileScannerTest {

    companion object {

        private lateinit var rootPath: String

        @BeforeClass
        @JvmStatic
        fun findDirs() {
            val url = FileScannerTest::class.java.classLoader.getResource("io/craigmiller160/videomanagerserver/file/")
            rootPath = url.toURI().path
        }

    }

    private lateinit var videoConfig: VideoConfiguration

    @Mock
    private lateinit var videoFileRepo: VideoFileRepository

    private lateinit var fileScanner: FileScanner

    @Mock
    private lateinit var settingsService: SettingsService

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        videoConfig = VideoConfiguration()
        videoConfig.fileExts = "txt,csv"
        fileScanner = FileScanner(videoConfig, videoFileRepo, settingsService)
    }

    @Test(expected = InvalidSettingException::class)
    fun test_scanForFiles_noRootDir() {
        `when`(settingsService.getOrCreateSettings())
                .thenReturn(Settings())

        val done = AtomicBoolean(false)

        fileScanner.scanForFiles {
            done.set(true)
        }
    }

    @Test
    fun test_scanForFiles() {
        val settings = Settings(
                settingsId = SETTINGS_ID,
                rootDir = rootPath
        )
        `when`(settingsService.getOrCreateSettings())
                .thenReturn(settings)

        runBlocking {
            val start = LocalDateTime.now()

            val done = AtomicBoolean(false)

            val job = fileScanner.scanForFiles {
                done.set(true)
            }

            withTimeout(10_000) {
                job.join()
            }

            assertTrue(done.get())

            // This insanity is from needing a separate library to handle kotlin null safety and some mocking methods
            val argumentCaptor = argumentCaptor<VideoFile>().apply {
                verify(videoFileRepo, times(4)).save(capture())
            }

            verify(videoFileRepo, times(1))
                    .setOldFilesInactive(any())

            val allValues = argumentCaptor.allValues
            val allValuesSorted = allValues.sortedBy { it.fileName }
            assertThat(allValuesSorted, allOf(
                    hasSize(equalTo(4)),
                    contains(
                            allOf(
                                    hasProperty("fileName", `is`("myFile.txt")),
                                    hasProperty("fileAdded", greaterThan(start)),
                                    hasProperty("active", equalTo(true))
                            ),
                            allOf(
                                    hasProperty("fileName", `is`("myFile2.txt")),
                                    hasProperty("fileAdded", greaterThan(start)),
                                    hasProperty("active", equalTo(true))
                            ),
                            allOf(
                                    hasProperty("fileName", `is`("otherExt.csv")),
                                    hasProperty("fileAdded", greaterThan(start)),
                                    hasProperty("active", equalTo(true))
                            ),
                            allOf(
                                    hasProperty("fileName", `is`("subdir/subDirFile.txt")),
                                    hasProperty("fileAdded", greaterThan(start)),
                                    hasProperty("active", equalTo(true))
                            )
                    )
            ))
        }
    }

}
