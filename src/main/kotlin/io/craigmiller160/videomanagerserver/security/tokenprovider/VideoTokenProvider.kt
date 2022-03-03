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
import io.craigmiller160.videomanagerserver.security.VideoTokenAuthentication
import io.craigmiller160.videomanagerserver.util.parseQueryString
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.GeneralSecurityException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.servlet.http.HttpServletRequest

@Component
class VideoTokenProvider (
        private val tokenConfig: TokenConfig
) : TokenProvider {

    private val encryptHandler: EncryptHandler

    init {
        encryptHandler = AesEncryptHandler(tokenConfig.secretKey, true)
    }

    companion object {
        private val EXP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    private fun generateExpiration(): String {
        val now = LocalDateTime.now()
        val exp = now.plusSeconds(tokenConfig.videoExpSecs.toLong())
        return EXP_FORMATTER.format(exp)
    }

    private fun getTokenRegex(): Regex {
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        return """.+$separator\d{1,10}$separator\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$separator.+""".toRegex()
    }

    override fun createToken(userName: String, params: Map<String,Any>): String {
        val videoId = params[TokenConstants.PARAM_VIDEO_ID]
        val fullFilePath = params[TokenConstants.PARAM_FILE_PATH]
        val exp = generateExpiration()
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val tokenString = "$userName$separator$videoId$separator$exp$separator$fullFilePath"
        return encryptHandler.doEncrypt(tokenString)
    }

    override fun resolveToken(req: HttpServletRequest): String? {
        val queryString = req.queryString ?: ""
        val queryParams = parseQueryString(queryString)
        return queryParams[TokenConstants.QUERY_PARAM_VIDEO_TOKEN]
    }

    override fun validateToken(token: String, params: Map<String,Any>): TokenValidationStatus {
        if (token.isEmpty()) {
            return TokenValidationStatus.NO_TOKEN
        }

        val tokenDecrypted: String
        try {
            tokenDecrypted = encryptHandler.doDecrypt(token)
            if (!getTokenRegex().matches(tokenDecrypted)) {
                return TokenValidationStatus.BAD_SIGNATURE
            }
        }
        catch (ex: GeneralSecurityException) {
            return TokenValidationStatus.BAD_SIGNATURE
        }

        val tokenParts = tokenDecrypted.split(TokenConstants.VIDEO_TOKEN_SEPARATOR)
        try {
            val expDateTime = LocalDateTime.parse(tokenParts[2], EXP_FORMATTER)
            val now = LocalDateTime.now()
            if (now > expDateTime) {
                return TokenValidationStatus.EXPIRED
            }
        }
        catch (ex: DateTimeParseException) {
            return TokenValidationStatus.EXPIRED
        }

        val videoId = params[TokenConstants.PARAM_VIDEO_ID]
        if (videoId != tokenParts[1]) {
            return TokenValidationStatus.RESOURCE_FORBIDDEN
        }

        return TokenValidationStatus.VALID
    }

    override fun createAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val userDetails = User.withUsername(claims[TokenConstants.CLAIM_SUBJECT] as String)
                .password("")
                .authorities(ArrayList<GrantedAuthority>())
                .build()
        return VideoTokenAuthentication(userDetails, claims)
    }

    override fun getClaims(token: String): Map<String, Any> {
        val tokenString = encryptHandler.doDecrypt(token)
        val tokenParams = tokenString.split(TokenConstants.VIDEO_TOKEN_SEPARATOR)
        return mapOf(
                TokenConstants.CLAIM_SUBJECT to tokenParams[0],
                TokenConstants.CLAIM_VIDEO_ID to tokenParams[1],
                TokenConstants.CLAIM_EXP to tokenParams[2],
                TokenConstants.CLAIM_FILE_PATH to tokenParams[3]
        )
    }
}
