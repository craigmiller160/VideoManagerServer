package io.craigmiller160.videomanagerserver.jwt

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.User
import io.craigmiller160.videomanagerserver.service.security.VideoUserDetailsService
import io.craigmiller160.videomanagerserver.util.LegacyDateConverter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import kotlin.collections.HashMap

// TODO needs tests

@Component
class JwtTokenProvider (
       private val tokenConfig: TokenConfig,
       private val videoUserDetailsService: VideoUserDetailsService,
       private val legacyDateConverter: LegacyDateConverter
) {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        const val ISSUER = "VideoManagerServer"
    }

    private var secretKey: String = ""

    @PostConstruct
    internal fun init() {
        secretKey = Base64.getEncoder().encodeToString(tokenConfig.key.toByteArray())
    }

    private fun generateExpiration(): Date {
        val now = LocalDateTime.now()
        val exp = now.plusSeconds(tokenConfig.expSecs.toLong())
        return legacyDateConverter.convertLocalDateTimeToDate(exp)
    }

    fun createToken(user: User): String {
        val claims = JWTClaimsSet.Builder()
                .subject(user.userName)
                .issueTime(Date())
                .issuer(ISSUER)
                .expirationTime(generateExpiration())
                .jwtID(UUID.randomUUID().toString())
                .notBeforeTime(Date())
                .build()
        val header = JWSHeader.Builder(JWSAlgorithm.HS256)
                .build()
        val jwt = SignedJWT(header, claims)
        val signer = MACSigner(secretKey)
        jwt.sign(signer)
        return jwt.serialize()
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader(AUTHORIZATION_HEADER)
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.replace(Regex("^$BEARER_PREFIX"), "")
        }
        return null
    }

    fun validateToken(token: String): Boolean {
        if (token.isEmpty()) {
            return false
        }

        val jwt = SignedJWT.parse(token)
        val verifier = MACVerifier(secretKey)
        return jwt.verify(verifier)
    }

    fun getUsername(token: String): String {
        // TODO actually get the username from the JWT
        val decoded = Base64.getDecoder().decode(token)
        return String(decoded)
    }

    fun getAuthentication(token: String): Authentication {
        // TODO actually build authentication here
        val userDetails = videoUserDetailsService.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }


    // TODO finish implementing the parts that I need here

}