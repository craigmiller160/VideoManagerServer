package io.craigmiller160.videomanagerserver.config

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals

@RunWith(SpringRunner::class)
@SpringBootTest
class TokenConfigIntegrationTest {

    companion object {
        private const val EXP_SECS = 300
        private const val VIDEO_EXP_SECS = 10000
        private const val REFRESH_EXP_SECS = 1200
        private const val KEY_SIZE_BITS = 256
    }

    @Autowired
    private lateinit var tokenConfig: TokenConfig

    @Test
    fun test_expSecs() {
        assertEquals(EXP_SECS, tokenConfig.expSecs)
    }

    @Test
    fun test_videoExpSecs() {
        assertEquals(VIDEO_EXP_SECS, tokenConfig.videoExpSecs)
    }

    @Test
    fun test_refreshExpSecs() {
        assertEquals(REFRESH_EXP_SECS, tokenConfig.refreshExpSecs)
    }

    @Test
    fun test_keySizeBits() {
        assertEquals(KEY_SIZE_BITS, tokenConfig.keySizeBits)
    }

}
