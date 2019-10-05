package io.craigmiller160.videomanagerserver.service.security

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class SecurityContextServiceTest {

    private lateinit var securityContext: SecurityContext
    private lateinit var authentication: Authentication
    private lateinit var securityContextService: SecurityContextService

    @Before
    fun setup() {
        securityContext = mock(SecurityContext::class.java)
        authentication = mock(Authentication::class.java)
        `when`(securityContext.authentication)
                .thenReturn(authentication)

        SecurityContextHolder.setContext(securityContext)

        securityContextService = SecurityContextService()
    }

    @After
    fun cleanup() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun test_getSecurityContext() {
        val result = securityContextService.getSecurityContext()
        assertEquals(securityContext, result)
    }

    @Test
    fun test_getUserName_userDetails() {
        val userName = "userName"
        val userDetails = mock(UserDetails::class.java)
        `when`(userDetails.username)
                .thenReturn(userName)
        `when`(authentication.principal)
                .thenReturn(userDetails)

        val result = securityContextService.getUserName()
        assertEquals(userName, result)
    }

    @Test
    fun test_getUserName_string() {
        val userName = "userName"
        `when`(authentication.principal)
                .thenReturn(userName)

        val result = securityContextService.getUserName()
        assertEquals(userName, result)
    }

}