package io.craigmiller160.videomanagerserver.security.tokenprovider

import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import org.springframework.security.core.Authentication
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

    private val cipher: Cipher

    init {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)

        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, tokenConfig.secretKey, ivSpec)
    }

    override fun createToken(user: AppUser, params: Map<String,Any>): String {
        val userName = user.userName
        val videoId = params[TokenConstants.PARAM_VIDEO_ID]
        val exp = tokenConfig.videoExpSecs
        val separator = TokenConstants.VIDEO_TOKEN_SEPARATOR
        val tokenString = "$userName$separator$videoId$separator$exp"
        return Base64.getEncoder().encodeToString(cipher.doFinal(tokenString.toByteArray()))
    }

    override fun resolveToken(req: HttpServletRequest): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateToken(token: String): TokenValidationStatus {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createAuthentication(token: String): Authentication {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getClaims(token: String): Map<String, Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRefreshAllowed(user: AppUser): Boolean {
        return false
    }
}