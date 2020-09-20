/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.security.tokenprovider

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.entity.Role
import io.craigmiller160.videomanagerserver.security.AuthGrantedAuthority
import io.craigmiller160.videomanagerserver.security.COOKIE_NAME
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider.Companion.ISSUER
import io.craigmiller160.videomanagerserver.util.LegacyDateConverter
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
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
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.Date
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

@RunWith(MockitoJUnitRunner::class)
class JwtTokenProviderTest {

    companion object {
        private const val EXP_SECS = 300
        private const val KEY = "ThisIsMySecretKeyThisIsMySecretKey"
        private const val USER_NAME = "userName"
        private const val ROLE = "MyRole"
        private const val SLEEP_MILLIS = 1000L
    }

    @Mock
    private lateinit var tokenConfig: TokenConfig

    @Spy
    private val legacyDateConverter = LegacyDateConverter()

    @InjectMocks
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Before
    fun setup() {
        `when`(tokenConfig.expSecs)
                .thenReturn(EXP_SECS)
        `when`(tokenConfig.keyString)
                .thenReturn(KEY)
    }

    @Test
    fun test_createToken() {
        val startDate = Date()
        val expTime = legacyDateConverter.convertDateToLocalDateTime(startDate)
                .plusSeconds(EXP_SECS.toLong())
        val expDate = legacyDateConverter.convertLocalDateTimeToDate(expTime)
        Thread.sleep(SLEEP_MILLIS)
        val user = AppUser().apply {
            userName = USER_NAME
            roles = listOf(Role(name = ROLE))
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
        assertEquals(listOf(ROLE), claimSet.getStringListClaim("roles"))
    }

    @Test
    fun test_validateToken_empty() {
        val result = jwtTokenProvider.validateToken("")
        assertEquals(TokenValidationStatus.NO_TOKEN, result)
    }

    private fun createToken(key: String, exp: Long): String {
        val expTime = LocalDateTime.now().plusSeconds(exp)
        val expDate = legacyDateConverter.convertLocalDateTimeToDate(expTime)
        val claims = JWTClaimsSet.Builder()
                .expirationTime(expDate)
                .subject(USER_NAME)
                .claim("roles", listOf(ROLE))
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
        assertEquals(TokenValidationStatus.BAD_SIGNATURE, result)
    }

    @Test
    fun test_validateToken_expired() {
        val token = createToken(KEY, -100L)
        val result = jwtTokenProvider.validateToken(token)
        assertEquals(TokenValidationStatus.EXPIRED, result)
    }

    @Test
    fun test_validateToken_valid() {
        val token = createToken(KEY, EXP_SECS.toLong())
        val result = jwtTokenProvider.validateToken(token)
        assertEquals(TokenValidationStatus.VALID, result)
    }

    @Test
    fun test_resolveToken_tokenExists() {
        val token = "ABCDEFG"
        val req = mock(HttpServletRequest::class.java)
        `when`(req.cookies)
                .thenReturn(arrayOf(Cookie(COOKIE_NAME, token)))
        val result = jwtTokenProvider.resolveToken(req)
        assertEquals(token, result)
    }

    @Test
    fun test_resolveToken_tokenMissing() {
        val req = mock(HttpServletRequest::class.java)
        `when`(req.cookies)
                .thenReturn(arrayOf())
        val result = jwtTokenProvider.resolveToken(req)
        assertNull(result)
    }

    @Test
    fun test_createAuthentication() {
        val token = createToken(KEY, EXP_SECS.toLong())
        val result = jwtTokenProvider.createAuthentication(token)
        assertThat(result, allOf(
                hasProperty("principal", allOf<UserDetails>(
                        hasProperty("username", equalTo(USER_NAME)),
                        hasProperty("authorities", hasSize<Collection<GrantedAuthority>>(1))
                )),
                hasProperty("authorities", allOf<Collection<GrantedAuthority>>(
                        hasSize(1),
                        contains(AuthGrantedAuthority(ROLE))
                ))
        ))
    }

    @Test
    fun test_getClaims() {
        val token = createToken(KEY, EXP_SECS.toLong())
        val claims = jwtTokenProvider.getClaims(token)
        assertEquals(USER_NAME, claims[TokenConstants.CLAIM_SUBJECT])
    }

    @Test
    fun test_isRefreshAllowed_refreshAllowed() {
        val user = AppUser().apply {
            lastAuthenticated = LocalDateTime.now().minusMinutes(1)
        }
        `when`(tokenConfig.refreshExpSecs)
                .thenReturn(1_200)

        assertTrue(jwtTokenProvider.isRefreshAllowed(user))
    }

    @Test
    fun test_isRefreshAllowed_refreshNotAllowed() {
        val user = AppUser().apply {
            lastAuthenticated = LocalDateTime.now().minusMinutes(1)
        }
        `when`(tokenConfig.refreshExpSecs)
                .thenReturn(10)

        assertFalse(jwtTokenProvider.isRefreshAllowed(user))
    }

    @Test
    fun test_isRefreshAllowed_nullLastAuthenticated() {
        val user = AppUser()
        assertFalse(jwtTokenProvider.isRefreshAllowed(user))
    }
}
