package io.craigmiller160.videomanagerserver.jwt

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.jwt.JwtTokenProvider.Companion.AUTHORIZATION_HEADER
import io.craigmiller160.videomanagerserver.jwt.JwtTokenProvider.Companion.ISSUER
import io.craigmiller160.videomanagerserver.service.security.VideoUserDetailsService
import io.craigmiller160.videomanagerserver.util.LegacyDateConverter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.hasProperty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.util.Base64
import java.util.Date
import javax.servlet.http.HttpServletRequest

@RunWith(MockitoJUnitRunner::class)
class JwtTokenProviderTest {

    companion object {
        private const val EXP_SECS = 300
        private const val KEY = "ThisIsMySecretKeyThisIsMySecretKey"
        private const val USER_NAME = "userName"
    }

    @Mock
    private lateinit var tokenConfig: TokenConfig

    @Mock
    private lateinit var videoUserDetailsService: VideoUserDetailsService

    @Spy
    private val legacyDateConverter = LegacyDateConverter()

    @InjectMocks
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Before
    fun setup() {
        `when`(tokenConfig.expSecs)
                .thenReturn(EXP_SECS)
        `when`(tokenConfig.key)
                .thenReturn(KEY)
        jwtTokenProvider.init()
    }

    @Test
    fun test_createToken() {
        val startDate = Date()
        val expTime = legacyDateConverter.convertDateToLocalDateTime(startDate)
                .plusSeconds(EXP_SECS.toLong())
        val expDate = legacyDateConverter.convertLocalDateTimeToDate(expTime)
        Thread.sleep(1000)
        val user = User().apply {
            userName = USER_NAME
        }
        val token = jwtTokenProvider.createToken(user)
        val jwt = SignedJWT.parse(token)
        val header = jwt.header
        val claimSet = jwt.jwtClaimsSet
        assertThat(header, hasProperty("algorithm", equalTo(JWSAlgorithm.HS256)))
        assertThat(claimSet, allOf(
                hasProperty("issuer", equalTo(ISSUER)),
                hasProperty("subject", equalTo(USER_NAME)),
                hasProperty("issueTime", greaterThan(startDate)),
                hasProperty("expirationTime", greaterThan(expDate)),
                hasProperty("notBeforeTime", greaterThan(startDate))
        ))
    }

    @Test
    fun test_validateToken_empty() {
        val result = jwtTokenProvider.validateToken("")
        assertFalse(result)
    }

    private fun createToken(key: String, exp: Long): String {
        val expTime = LocalDateTime.now().plusSeconds(exp)
        val expDate = legacyDateConverter.convertLocalDateTimeToDate(expTime)
        val claims = JWTClaimsSet.Builder()
                .expirationTime(expDate)
                .build()
        val header = JWSHeader(JWSAlgorithm.HS256)
        val jwt = SignedJWT(header, claims)
        val signer = MACSigner(key)
        jwt.sign(signer)
        return jwt.serialize()
    }

    @Test
    fun test_validateToken_invalidSignature() {
        val token = createToken("BadSecretKeyBadSecretKeyBadSecretKey", EXP_SECS.toLong())
        val result = jwtTokenProvider.validateToken(token)
        assertFalse(result)
    }

    @Test
    fun test_validateToken_expired() {
        val secretKey = Base64.getEncoder().encodeToString(KEY.toByteArray())
        val token = createToken(secretKey, 0L)
        Thread.sleep(1000)
        val result = jwtTokenProvider.validateToken(token)
        assertFalse(result)
    }

    @Test
    fun test_validateToken_valid() {
        val secretKey = Base64.getEncoder().encodeToString(KEY.toByteArray())
        val token = createToken(secretKey, EXP_SECS.toLong())
        val result = jwtTokenProvider.validateToken(token)
        assertTrue(result)
    }

    @Test
    fun test_resolveToken_tokenExists() {
        val token = "ABCDEFG"
        val req = mock(HttpServletRequest::class.java)
        `when`(req.getHeader(AUTHORIZATION_HEADER))
                .thenReturn("Bearer $token")
        val result = jwtTokenProvider.resolveToken(req)
        assertEquals(token, result)
    }

    @Test
    fun test_resolveToken_tokenMissing() {
        val req = mock(HttpServletRequest::class.java)
        val result = jwtTokenProvider.resolveToken(req)
        assertNull(result)
    }

    @Test
    fun test_resolveToken_noBearer() {
        val req = mock(HttpServletRequest::class.java)
        `when`(req.getHeader(AUTHORIZATION_HEADER))
                .thenReturn("ABCDEFG")
        val result = jwtTokenProvider.resolveToken(req)
        assertNull(result)
    }
}