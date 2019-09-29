package io.craigmiller160.videomanagerserver.security.tokenprovider

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VideoTokenProviderTest {

    @Mock
    private lateinit var tokenConfig: TokenConfig

    @InjectMocks
    private lateinit var videoTokenProvider: VideoTokenProvider

    @Test
    fun test_createToken() {
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
