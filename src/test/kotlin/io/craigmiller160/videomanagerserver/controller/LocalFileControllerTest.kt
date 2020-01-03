package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.LocalFile
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.security.ROLE_ADMIN
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.file.LocalFileService
import io.craigmiller160.videomanagerserver.test_util.responseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
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

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var jacksonLocalFileList: JacksonTester<List<LocalFile>>

    @Before
    fun setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)

        JacksonTester.initFields(this, objectMapper)
    }

    private fun mockFiles(): List<LocalFile> {
        val file1 = LocalFile(
                fileName = "file1",
                filePath = "dir/file1",
                isDirectory = false
        )
        val file2 = LocalFile(
                fileName = "file2",
                filePath = "dir/file2",
                isDirectory = false
        )
        val dir1 = LocalFile(
                fileName = "dir1",
                filePath = "dir/dir1",
                isDirectory = true
        )
        return listOf(file1, file2, dir1)
    }

    @Test
    fun test_getFilesFromDirectory() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )

        val path = "dir"
        val files = mockFiles()
        `when`(localFileService.getFilesFromDirectory(path))
                .thenReturn(files)

        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doGet("/api/localfiles/directory?path=$path")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonLocalFileList.write(files).json))
        ))
    }

    @Test
    fun test_getFilesFromDirectory_noFiles() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )

        val path = "dir"
        `when`(localFileService.getFilesFromDirectory(path))
                .thenReturn(listOf())

        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doGet("/api/localfiles/directory?path=$path")
        assertThat(response, allOf(
                hasProperty("status", equalTo(204))
        ))
    }

    @Test
    fun test_getFilesFromDirectory_noPath() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )

        val files = mockFiles()
        `when`(localFileService.getFilesFromDirectory(null))
                .thenReturn(files)

        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        var response = mockMvcHandler.doGet("/api/localfiles/directory")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonLocalFileList.write(files).json))
        ))

        response = mockMvcHandler.doGet("/api/localfiles/directory?path=")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonLocalFileList.write(files).json))
        ))
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
