package io.craigmiller160.videomanagerserver.file

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
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

            val done = AtomicBoolean(false)

            val result = async {
                fileScanner.scanForFiles {
                    done.set(true)
                }
            }

            withTimeout(10_000) {
                result.await()
            }

            assertTrue(done.get())

            // This insanity is from needing a separate library to handle kotlin null safety and some mocking methods
            val argumentCaptor = argumentCaptor<String>().apply {
                verify(videoFileRepo, times(4)).mergeVideoFilesByName(capture())
            }

            val allValues = argumentCaptor.allValues
            assertEquals(4, allValues.size)
            assertThat(allValues, containsInAnyOrder("subdir/subDirFile.txt", "myFile.txt", "myFile2.txt", "otherExt.csv"))
        }
    }

}