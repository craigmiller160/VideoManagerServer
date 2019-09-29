package io.craigmiller160.videomanagerserver.security.tokenprovider

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.crypto.AesEncryptHandler
import io.craigmiller160.videomanagerserver.crypto.EncryptHandler
import io.craigmiller160.videomanagerserver.dto.AppUser
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class VideoTokenProviderTest {

    companion object {
        const val USER_NAME = "userName"
        const val VIDEO_ID = "10"
        private const val KEY = "XaTw9UVgImYHxi/jXwrq3hMWHsWsnkNC6iWszHzut/U="
        private val EXP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    @Mock
    private lateinit var tokenConfig: TokenConfig

    private lateinit var videoTokenProvider: VideoTokenProvider

    private lateinit var secretKey: SecretKey

    private lateinit var aesEncryptHandler: EncryptHandler

    @Before
    fun setup() {
        val keyBytes = Base64.getDecoder().decode(KEY)
        this.secretKey = SecretKeySpec(keyBytes, 0, keyBytes.size, "AES")
        aesEncryptHandler = AesEncryptHandler(this.secretKey)
        `when`(tokenConfig.secretKey)
                .thenReturn(secretKey)
        videoTokenProvider = VideoTokenProvider(tokenConfig)
    }

    @Test
    fun test_createToken() {
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val dateRegex = """\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}"""
        val tokenRegex = "$USER_NAME$separator$VIDEO_ID$separator$dateRegex".toRegex()
        val appUser = AppUser(userName = USER_NAME)
        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to VIDEO_ID)
        val token = videoTokenProvider.createToken(appUser, params)
        val tokenDecrypted = aesEncryptHandler.doDecrypt(token)
        assertTrue("No match: $tokenDecrypted") { tokenRegex.matches(tokenDecrypted) }
    }

    @Test
    fun test_resolveToken() {
        val token = "ABCDEFG"
        val queryString = "${TokenConstants.QUERY_PARAM_VIDEO_TOKEN}=$token"
        val req = mock(HttpServletRequest::class.java)
        `when`(req.queryString)
                .thenReturn(queryString)

        val result = videoTokenProvider.resolveToken(req)
        assertEquals(token, result)
    }

    @Test
    fun test_resolveToken_noToken() {
        val req = mock(HttpServletRequest::class.java)
        val result = videoTokenProvider.resolveToken(req)
        assertNull(result)
    }

    @Test
    fun test_validateToken_empty() {
        val result = videoTokenProvider.validateToken("")
        assertEquals(TokenValidationStatus.NO_TOKEN, result)
    }

    @Test
    fun test_validateToken_badSignature() {
        val token = "ABCDEFGHIJLKMNO"
        val result = videoTokenProvider.validateToken(token)
        assertEquals(TokenValidationStatus.BAD_SIGNATURE, result)
    }

    @Test
    fun test_validateToken_expired() {
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val date = LocalDateTime.of(2001, 1, 1, 1, 1, 1)
        val dateString = EXP_FORMATTER.format(date)
        val token = "$USER_NAME$separator$VIDEO_ID$separator$dateString"
        val tokenEncrypted = aesEncryptHandler.doEncrypt(token)
        val result = videoTokenProvider.validateToken(tokenEncrypted)
        assertEquals(TokenValidationStatus.EXPIRED, result)
    }

    @Test
    fun test_valdateToken_resourceForbidden() {
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val date = LocalDateTime.now().plusHours(10)
        val dateString = EXP_FORMATTER.format(date)
        val token = "$USER_NAME$separator$VIDEO_ID$separator$dateString"
        val tokenEncrypted = aesEncryptHandler.doEncrypt(token)
        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to "11")
        val result = videoTokenProvider.validateToken(tokenEncrypted, params)
        assertEquals(TokenValidationStatus.RESOURCE_FORBIDDEN, result)
    }

    @Test
    fun test_validateToken_valid() {
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val date = LocalDateTime.now().plusHours(10)
        val dateString = EXP_FORMATTER.format(date)
        val token = "$USER_NAME$separator$VIDEO_ID$separator$dateString"
        val tokenEncrypted = aesEncryptHandler.doEncrypt(token)
        val params = mapOf(TokenConstants.PARAM_VIDEO_ID to VIDEO_ID)
        val result = videoTokenProvider.validateToken(tokenEncrypted, params)
        assertEquals(TokenValidationStatus.VALID, result)
    }

    @Test
    fun test_createAuthentication() {
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val date = LocalDateTime.now().plusHours(10)
        val dateString = EXP_FORMATTER.format(date)
        val token = "$USER_NAME$separator$VIDEO_ID$separator$dateString"
        val tokenEncrypted = aesEncryptHandler.doEncrypt(token)

        val result = videoTokenProvider.createAuthentication(tokenEncrypted)
        assertThat(result, allOf(
                hasProperty("principal", allOf<UserDetails>(
                        hasProperty("username", equalTo(USER_NAME)),
                        hasProperty("authorities", hasSize<Collection<GrantedAuthority>>(0))
                ))
        ))
    }

    @Test
    fun test_getClaims() {
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val date = LocalDateTime.now().plusHours(10)
        val dateString = EXP_FORMATTER.format(date)
        val token = "$USER_NAME$separator$VIDEO_ID$separator$dateString"
        val tokenEncrypted = aesEncryptHandler.doEncrypt(token)

        val claims = videoTokenProvider.getClaims(tokenEncrypted)
        assertThat(claims, allOf<Map<String,Any>>(
                hasEntry(TokenConstants.CLAIM_SUBJECT, USER_NAME),
                hasEntry(TokenConstants.CLAIM_VIDEO_ID, VIDEO_ID),
                hasEntry(TokenConstants.CLAIM_EXP, dateString)
        ))
    }

    @Test
    fun test_isRefreshAllowed() {
        assertFalse(videoTokenProvider.isRefreshAllowed(AppUser()))
    }

}
