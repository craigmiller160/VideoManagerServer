package io.craigmiller160.videomanagerserver.config

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class VideoConfigurationIntegrationTest {

    companion object {
        private const val VLC_COMMAND = "VLC_COMMAND"
        private const val FILE_PATH_ROOT = "FILE_PATH_ROOT"
        private const val PAGE_SIZE = 10
    }

    @Autowired
    private lateinit var videoConfig: VideoConfiguration

    @Test
    fun testVlcCommand() {
        assertEquals(VLC_COMMAND, videoConfig.vlcCommand)
    }

    @Test
    fun testFilePathRoot() {
        assertEquals(FILE_PATH_ROOT, videoConfig.filePathRoot)
    }

    @Test
    fun testApiPageSize() {
        assertEquals(PAGE_SIZE, videoConfig.apiPageSize)
    }

}