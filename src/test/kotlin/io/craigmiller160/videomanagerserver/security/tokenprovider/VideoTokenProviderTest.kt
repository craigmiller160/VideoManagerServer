package io.craigmiller160.videomanagerserver.security.tokenprovider

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RunWith(MockitoJUnitRunner::class)
class VideoTokenProviderTest {

    companion object {
        const val USER_NAME = "userName"
        const val VIDEO_ID = "10"
        private const val KEY = "ThisIsMySecretKeyThisIsMySecretKey"
    }

    @Mock
    private lateinit var tokenConfig: TokenConfig

    @InjectMocks
    private lateinit var videoTokenProvider: VideoTokenProvider

    private lateinit var secretKey: SecretKey

    @Before
    fun setup() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        this.secretKey = keyGen.generateKey()
        `when`(tokenConfig.secretKey)
                .thenReturn(secretKey)
    }

    @Test
    fun test_createToken() {
        val appUser = AppUser(userName = USER_NAME)
        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to VIDEO_ID)
        val token = videoTokenProvider.createToken(appUser, params)
//        println(videoTokenProvider.doDecrypt(token)) // TODO delete this
        TODO("Finish this")
    }

    @Test
    fun test_resolveToken() {
        TODO("Finish this")
    }

    @Test
    fun test_resolveToken_noToken() {
        TODO("Finish this")
    }

    @Test
    fun test_validateToken_empty() {
        TODO("Finish this")
    }

    @Test
    fun test_validateToken_badSignature() {
        TODO("Finish this")
    }

    @Test
    fun test_validateToken_expired() {
        TODO("Finish this")
    }

    @Test
    fun test_valdateToken_resourceForbidden() {
        TODO("Finish this")
    }

    @Test
    fun test_validateToken_valid() {
        TODO("Finish this")
    }

    @Test
    fun test_createAuthentication() {
        TODO("Finish this")
    }

    @Test
    fun test_getClaims() {
        TODO("Finish this")
    }

    @Test
    fun test_isRefreshAllowed() {
        assertFalse(videoTokenProvider.isRefreshAllowed(AppUser()))
    }

}
