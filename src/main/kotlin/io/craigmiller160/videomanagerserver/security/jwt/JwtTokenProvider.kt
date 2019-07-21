package io.craigmiller160.videomanagerserver.security.jwt

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.util.LegacyDateConverter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Base64
import java.util.Date
import java.util.UUID
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider (
       private val tokenConfig: TokenConfig,
       private val legacyDateConverter: LegacyDateConverter
) {

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
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

    fun createToken(user: AppUser): String {
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
        if (jwt.verify(verifier)) {
            val exp = legacyDateConverter.convertDateToLocalDateTime(jwt.jwtClaimsSet.expirationTime)
            val now = LocalDateTime.now()
            return exp >= now
        }
        return false
    }

    fun getAuthentication(token: String): Authentication {
        val jwt = SignedJWT.parse(token)
        val claims = jwt.jwtClaimsSet
        val userDetails = org.springframework.security.core.userdetails.User
                .withUsername(claims.subject)
                .password("")
                .authorities("Admin") // TODO going to want to customize this
                .build()
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

}