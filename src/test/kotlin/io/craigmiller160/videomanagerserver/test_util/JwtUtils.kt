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

package io.craigmiller160.videomanagerserver.test_util

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.craigmiller160.oauth2.security.AuthenticatedUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object JwtUtils {

    const val ROLE_EDIT = "ROLE_EDIT"
    const val ROLE_ADMIN = "ROLE_ADMIN"
    const val ROLE_SCAN = "ROLE_SCAN"
    const val USERNAME = "username"
    const val ROLES_CLAIM = "roles"
    const val CLIENT_KEY = "clientKey"
    const val CLIENT_NAME = "clientName"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val TOKEN_ID = "JWTID"
    const val USER_ID = "1"

    fun createKeyPair(): KeyPair {
        val keyPairGen = KeyPairGenerator.getInstance("RSA")
        return keyPairGen.genKeyPair()
    }

    fun createJwkSet(keyPair: KeyPair): JWKSet {
        val builder = RSAKey.Builder(keyPair.public as RSAPublicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID("oauth-jwt")
        return JWKSet(builder.build())
    }

    private fun doCreateJwt(expMinutes: Long = 100, roles: List<String> = listOf()): SignedJWT {
        val header = JWSHeader.Builder(JWSAlgorithm.RS256)
                .build()

        val exp = LocalDateTime.now().plusMinutes(expMinutes)
        val expDate = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant())

        val claims = JWTClaimsSet.Builder()
                .jwtID(TOKEN_ID)
                .issueTime(Date())
                .subject(USERNAME)
                .expirationTime(expDate)
                .claim(ROLES_CLAIM, roles)
                .claim("clientKey", CLIENT_KEY)
                .claim("clientName", CLIENT_NAME)
                .claim("firstName", FIRST_NAME)
                .claim("lastName", LAST_NAME)
                .claim("userId", USER_ID)
                .build()
        return SignedJWT(header, claims)
    }

    fun createJwt(expMinutes: Long = 100): SignedJWT {
        return doCreateJwt(expMinutes)
    }

    fun createEditJwt(expMinutes: Long = 1000): SignedJWT {
        return doCreateJwt(expMinutes, listOf(ROLE_EDIT))
    }

    fun createScanJwt(expMinutes: Long = 1000): SignedJWT {
        return doCreateJwt(expMinutes, listOf(ROLE_SCAN))
    }

    fun createAdminJwt(expMinutes: Long = 1000): SignedJWT {
        return doCreateJwt(expMinutes, listOf(ROLE_ADMIN))
    }

    fun signAndSerializeJwt(jwt: SignedJWT, privateKey: PrivateKey): String {
        val signer = RSASSASigner(privateKey)
        jwt.sign(signer)
        return jwt.serialize()
    }

}