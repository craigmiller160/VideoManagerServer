package io.craigmiller160.videomanagerserver.file

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.VideoFile
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mock
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

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        videoConfig = VideoConfiguration()
        videoConfig.filePathRoot = rootPath
        videoConfig.fileExts = "txt,csv"
        fileScanner = FileScanner(videoConfig, videoFileRepo)
    }

    @Test
    fun testScanForFiles() {
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

            val allValues = argumentCaptor.allValues
            val allValuesSorted = allValues.sortedBy { it.fileName }
            assertThat(allValuesSorted, allOf(
                    hasSize(equalTo(4)),
                    contains(
                            allOf(
                                    hasProperty("fileName", `is`("myFile.txt")),
                                    hasProperty("fileAdded", greaterThan(start))
                            ),
                            allOf(
                                    hasProperty("fileName", `is`("myFile2.txt")),
                                    hasProperty("fileAdded", greaterThan(start))
                            ),
                            allOf(
                                    hasProperty("fileName", `is`("otherExt.csv")),
                                    hasProperty("fileAdded", greaterThan(start))
                            ),
                            allOf(
                                    hasProperty("fileName", `is`("subdir/subDirFile.txt")),
                                    hasProperty("fileAdded", greaterThan(start))
                            )
                    )
            ))
        }
    }

}