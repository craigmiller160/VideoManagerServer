package io.craigmiller160.videomanagerserver.jwt

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.jwt.JwtTokenProvider.Companion.ISSUER
import io.craigmiller160.videomanagerserver.service.security.VideoUserDetailsService
import io.craigmiller160.videomanagerserver.util.LegacyDateConverter
import junit.framework.Assert.assertEquals
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

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
}