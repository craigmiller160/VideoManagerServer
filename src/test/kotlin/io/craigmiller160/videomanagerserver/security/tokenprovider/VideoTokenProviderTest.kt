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

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.crypto.AesEncryptHandler
import io.craigmiller160.videomanagerserver.crypto.EncryptHandler
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@ExtendWith(MockitoExtension::class)
class VideoTokenProviderTest {

  companion object {
    private const val USER_NAME = "userName"
    private const val VIDEO_ID = "10"
    private const val USER_ID = 1L
    private const val FILE_PATH = "/full/file/path"
    private const val KEY = "XaTw9UVgImYHxi/jXwrq3hMWHsWsnkNC6iWszHzut/U="
    private val EXP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  }

  @Mock private lateinit var tokenConfig: TokenConfig

  private lateinit var videoTokenProvider: VideoTokenProvider

  private lateinit var secretKey: SecretKey

  private lateinit var aesEncryptHandler: EncryptHandler

  @BeforeEach
  fun setup() {
    val keyBytes = Base64.getDecoder().decode(KEY)
    this.secretKey = SecretKeySpec(keyBytes, 0, keyBytes.size, "AES")
    aesEncryptHandler = AesEncryptHandler(this.secretKey, true)
    `when`(tokenConfig.secretKey).thenReturn(secretKey)
    videoTokenProvider = VideoTokenProvider(tokenConfig)
  }

  @Test
  fun test_createToken() {
    val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
    val dateRegex = """\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}"""
    val tokenRegex =
      "$USER_NAME$separator$USER_ID$separator$VIDEO_ID$separator$dateRegex$separator.*".toRegex()
    val params =
      mapOf(
        TokenConstants.PARAM_VIDEO_ID to VIDEO_ID,
        TokenConstants.PARAM_FILE_PATH to FILE_PATH,
        TokenConstants.PARAM_USER_ID to USER_ID)
    val token = videoTokenProvider.createToken("userName", params)
    val tokenDecrypted = aesEncryptHandler.doDecrypt(token)
    assertTrue("No match: $tokenDecrypted") { tokenRegex.matches(tokenDecrypted) }
  }

  @Test
  fun test_resolveToken() {
    val token = "ABCDEFG"
    val queryString = "${TokenConstants.QUERY_PARAM_VIDEO_TOKEN}=$token"
    val req = mock(HttpServletRequest::class.java)
    `when`(req.queryString).thenReturn(queryString)

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
    val token = "$USER_NAME$separator$VIDEO_ID$separator$dateString$separator$FILE_PATH"
    val tokenEncrypted = aesEncryptHandler.doEncrypt(token)
    val result = videoTokenProvider.validateToken(tokenEncrypted)
    assertEquals(TokenValidationStatus.EXPIRED, result)
  }

  @Test
  fun test_validateToken_valid() {
    val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
    val date = LocalDateTime.now().plusHours(10)
    val dateString = EXP_FORMATTER.format(date)
    val token =
      "$USER_NAME$separator$USER_ID$separator$VIDEO_ID$separator$dateString$separator$FILE_PATH"
    val tokenEncrypted = aesEncryptHandler.doEncrypt(token)
    val params =
      mapOf(TokenConstants.PARAM_VIDEO_ID to VIDEO_ID, TokenConstants.PARAM_USER_ID to USER_ID)
    val result = videoTokenProvider.validateToken(tokenEncrypted, params)
    assertEquals(TokenValidationStatus.VALID, result)
  }

  @Test
  fun test_createAuthentication() {
    val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
    val date = LocalDateTime.now().plusHours(10)
    val dateString = EXP_FORMATTER.format(date)
    val token =
      "$USER_NAME$separator$USER_ID$separator$VIDEO_ID$separator$dateString$separator$FILE_PATH"
    val tokenEncrypted = aesEncryptHandler.doEncrypt(token)

    val result = videoTokenProvider.createAuthentication(tokenEncrypted)
    assertThat(
      result,
      allOf(
        hasProperty(
          "principal",
          allOf<UserDetails>(
            hasProperty("username", equalTo(USER_NAME)),
            hasProperty(
              "authorities",
              contains<GrantedAuthority>(
                SimpleGrantedAuthority("ROLE_video-access"),
                SimpleGrantedAuthority("file_$VIDEO_ID"))),
          )),
        hasProperty("filePath", equalTo(FILE_PATH)),
        hasProperty("claims", aMapWithSize<String, Any>(4))))
    assertTrue { result.isAuthenticated }
  }

  @Test
  fun test_getClaims() {
    val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
    val date = LocalDateTime.now().plusHours(10)
    val dateString = EXP_FORMATTER.format(date)
    val token =
      "$USER_NAME$separator$USER_ID$separator$VIDEO_ID$separator$dateString$separator$FILE_PATH"
    val tokenEncrypted = aesEncryptHandler.doEncrypt(token)

    val claims = videoTokenProvider.getClaims(tokenEncrypted)
    assertThat(
      claims,
      allOf<Map<String, Any>>(
        hasEntry(TokenConstants.CLAIM_SUBJECT, USER_NAME),
        hasEntry(TokenConstants.CLAIM_VIDEO_ID, VIDEO_ID),
        hasEntry(TokenConstants.CLAIM_EXP, dateString),
        hasEntry(TokenConstants.CLAIM_FILE_PATH, FILE_PATH)))
  }
}
