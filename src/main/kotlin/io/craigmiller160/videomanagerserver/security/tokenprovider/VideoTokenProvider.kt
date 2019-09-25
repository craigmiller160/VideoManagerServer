package io.craigmiller160.videomanagerserver.security.tokenprovider

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.util.parseQueryString
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.servlet.http.HttpServletRequest

// TODO need unit tests for all methods here

@Component
class VideoTokenProvider (
        private val tokenConfig: TokenConfig
) : TokenProvider {

    companion object {
        private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    }

    private fun doEncrypt(value: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, tokenConfig.secretKey, ivSpec)
        return Base64.getEncoder().encodeToString(cipher.doFinal(value.toByteArray()))
    }

    private fun doDecrypt(value: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, tokenConfig.secretKey, ivSpec)
        val bytes = Base64.getDecoder().decode(value)
        return String(cipher.doFinal(bytes))
    }

    override fun createToken(user: AppUser, params: Map<String,Any>): String {
        val userName = user.userName
        val videoId = params[TokenConstants.PARAM_VIDEO_ID]
        val exp = tokenConfig.videoExpSecs
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val tokenString = "$userName$separator$videoId$separator$exp"
        return doEncrypt(tokenString)
    }

    override fun resolveToken(req: HttpServletRequest): String? {
        val queryString = req.queryString ?: ""
        val queryParams = parseQueryString(queryString)
        return queryParams[TokenConstants.QUERY_PARAM_VIDEO_TOKEN]
    }

    override fun validateToken(token: String, params: Map<String,Any>): TokenValidationStatus {
        // Validate that it isn't expired
        // Validate that it is for the current video - probably need optional params for this
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val userDetails = User.withUsername(claims[TokenConstants.CLAIM_SUBJECT] as String)
                .password("")
                .build()
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    override fun getClaims(token: String): Map<String, Any> {
        val tokenString = doDecrypt(token)
        val tokenParams = tokenString.split(TokenConstants.VIDEO_TOKEN_SEPARATOR)
        return mapOf(
                TokenConstants.CLAIM_SUBJECT to tokenParams[0],
                TokenConstants.CLAIM_VIDEO_ID to tokenParams[1],
                TokenConstants.CLAIM_EXP to tokenParams[2]
        )
    }

    override fun isRefreshAllowed(user: AppUser): Boolean {
        return false
    }
}