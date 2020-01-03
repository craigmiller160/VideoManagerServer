package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.service.file.LocalFileService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class LocalFileControllerTest {

    @MockBean
    private lateinit var localFileService: LocalFileService

    @Autowired
    private lateinit var localFileController: LocalFileController

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)
    }

    @Test
    fun test_getFilesFromDirectory() {
        TODO("Finish this")
    }

    @Test
    fun test_getFilesFromDirectory_noFiles() {
        TODO("Finish this")
    }

    @Test
    fun test_getFilesFromDirectory_noPath() {
        TODO("Finish this")
    }

    @Test
    fun test_getFilesFromDirectory_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_getFilesFromDirectory_missingRole() {
        TODO("Finish this")
    }

}
