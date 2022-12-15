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

package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.jwk.JWKSet
import io.craigmiller160.oauth2.config.OAuth2Config
import io.craigmiller160.videomanagerserver.test_util.JwtUtils
import org.junit.Before
import org.junit.BeforeClass
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.security.KeyPair

abstract class AbstractControllerTest {

    companion object {

        @JvmStatic
        protected lateinit var keyPair: KeyPair
        @JvmStatic
        protected lateinit var jwkSet: JWKSet

        @BeforeClass
        @JvmStatic
        fun beforeAll() {
            keyPair = JwtUtils.createKeyPair()
            jwkSet = JwtUtils.createJwkSet(keyPair)
        }
    }

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    protected lateinit var mockMvcHandler: MockMvcHandler

    @MockBean
    protected lateinit var oauthConfig: OAuth2Config

    protected lateinit var token: String
    protected lateinit var editToken: String
    protected lateinit var scanToken: String
    protected lateinit var adminToken: String

    @Before
    open fun setup() {
        Mockito.`when`(oauthConfig.jwkSet)
                .thenReturn(jwkSet)
        Mockito.`when`(oauthConfig.clientKey)
                .thenReturn(JwtUtils.CLIENT_KEY)
        Mockito.`when`(oauthConfig.clientName)
                .thenReturn(JwtUtils.CLIENT_NAME)
        Mockito.`when`(oauthConfig.cookieName)
                .thenReturn("vm_token")

        val jwt = JwtUtils.createJwt()
        token = JwtUtils.signAndSerializeJwt(jwt, keyPair.private)

        val editJwt = JwtUtils.createEditJwt()
        editToken = JwtUtils.signAndSerializeJwt(editJwt, keyPair.private)

        val scanJwt = JwtUtils.createScanJwt()
        scanToken = JwtUtils.signAndSerializeJwt(scanJwt, keyPair.private)

        val adminJwt = JwtUtils.createAdminJwt()
        adminToken = JwtUtils.signAndSerializeJwt(adminJwt, keyPair.private)

        mockMvcHandler = buildMockMvcHandler()
        JacksonTester.initFields(this, objectMapper)
    }

    protected fun buildMockMvcHandler(): MockMvcHandler {
        val mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        return MockMvcHandler(mockMvc)
    }


}
