package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.dto.LocalFile
import io.craigmiller160.videomanagerserver.dto.LocalFileList
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
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class LocalFileControllerTest : AbstractControllerTest() {

    @MockBean
    private lateinit var localFileService: LocalFileService

    @Autowired
    private lateinit var localFileController: LocalFileController

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var jacksonLocalFileList: JacksonTester<LocalFileList>

    private fun mockFiles(): LocalFileList {
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
        val files = listOf(file1, file2, dir1)
        return LocalFileList(
                rootPath = "dir",
                files = files
        )
    }

    @Test
    fun test_getFilesFromDirectory() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )

        val path = "dir"
        val files = mockFiles()
        `when`(localFileService.getFilesFromDirectory(path, false))
                .thenReturn(files)

        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doGet("/api/localfiles/directory?path=$path")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonLocalFileList.write(files).json))
        ))
    }

    @Test
    fun test_getFilesFromDirectory_noPath() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_ADMIN))
        )

        val files = mockFiles()
        `when`(localFileService.getFilesFromDirectory(null, false))
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
        val response = mockMvcHandler.doGet("/api/localfiles/directory")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_getFilesFromDirectory_missingRole() {
        val user = AppUser(
                userName = "userName"
        )

        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val response = mockMvcHandler.doGet("/api/localfiles/directory")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

}
