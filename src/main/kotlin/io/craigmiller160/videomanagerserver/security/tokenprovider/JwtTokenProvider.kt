package io.craigmiller160.videomanagerserver.security.tokenprovider

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.videomanagerserver.config.TokenConfig
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.security.AuthGrantedAuthority
import io.craigmiller160.videomanagerserver.security.COOKIE_NAME
import io.craigmiller160.videomanagerserver.util.LegacyDateConverter
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID
import javax.servlet.http.HttpServletRequest

@Component
class JwtTokenProvider (
       private val tokenConfig: TokenConfig,
       private val legacyDateConverter: LegacyDateConverter
) : TokenProvider {

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val ISSUER = "VideoManagerServer"
    }

    private fun generateExpiration(): Date {
        val now = LocalDateTime.now()
        val exp = now.plusSeconds(tokenConfig.expSecs.toLong())
        return legacyDateConverter.convertLocalDateTimeToDate(exp)
    }

    override fun createToken(user: AppUser, params: Map<String,Any>): String {
        val roles = user.roles.map { role -> role.name }
        val claims = JWTClaimsSet.Builder()
                .subject(user.userName)
                .issueTime(Date())
                .issuer(ISSUER)
                .expirationTime(generateExpiration())
                .jwtID(UUID.randomUUID().toString())
                .notBeforeTime(Date())
                .claim("roles", roles)
                .build()
        val header = JWSHeader.Builder(JWSAlgorithm.HS256)
                .build()
        val jwt = SignedJWT(header, claims)
        val signer = MACSigner(tokenConfig.keyString)
        jwt.sign(signer)
        return jwt.serialize()
    }

    override fun resolveToken(req: HttpServletRequest): String? {
        return req.cookies?.find { cookie -> cookie.name == COOKIE_NAME }?.value
    }

    private fun legacyAuthHeaderResolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader(AUTHORIZATION_HEADER)
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.replace(Regex("^$BEARER_PREFIX"), "")
        }
        return null
    }

    override fun validateToken(token: String, params: Map<String,Any>): TokenValidationStatus {
        if (token.isEmpty()) {
            return TokenValidationStatus.NO_TOKEN
        }

        val jwt = SignedJWT.parse(token)
        val verifier = MACVerifier(tokenConfig.keyString)
        if (jwt.verify(verifier)) {
            val exp = legacyDateConverter.convertDateToLocalDateTime(jwt.jwtClaimsSet.expirationTime)
            val now = LocalDateTime.now()
            if (exp >= now) {
                return TokenValidationStatus.VALID
            }
            return TokenValidationStatus.EXPIRED
        }
        return TokenValidationStatus.BAD_SIGNATURE
    }

    override fun createAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val authorities = (claims[TokenConstants.CLAIM_ROLES] as List<String>)
                .map { role -> AuthGrantedAuthority(role) }
        val userDetails = User.withUsername(claims[TokenConstants.CLAIM_SUBJECT] as String)
                .password("")
                .authorities(authorities)
                .build()
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    override fun getClaims(token: String): Map<String,Any> {
        val jwt = SignedJWT.parse(token)
        return jwt.jwtClaimsSet.claims
    }

    override fun isRefreshAllowed(user: AppUser): Boolean {
        val lastAuthenticated = user.lastAuthenticated ?: LocalDateTime.MIN
        val now = LocalDateTime.now()
        return now <= (lastAuthenticated.plusSeconds(tokenConfig.refreshExpSecs.toLong()))
    }

}
