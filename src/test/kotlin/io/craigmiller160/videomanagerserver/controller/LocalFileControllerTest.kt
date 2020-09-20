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

import io.craigmiller160.videomanagerserver.dto.LocalFileListResponse
import io.craigmiller160.videomanagerserver.dto.LocalFileResponse
import io.craigmiller160.videomanagerserver.service.file.LocalFileService
import io.craigmiller160.videomanagerserver.test_util.responseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class LocalFileControllerTest : AbstractControllerTest() {

    @MockBean
    private lateinit var localFileService: LocalFileService

    @Autowired
    private lateinit var localFileController: LocalFileController

    private lateinit var jacksonLocalFileList: JacksonTester<LocalFileListResponse>

    private fun mockFiles(): LocalFileListResponse {
        val file1 = LocalFileResponse(
                fileName = "file1",
                filePath = "dir/file1",
                isDirectory = false
        )
        val file2 = LocalFileResponse(
                fileName = "file2",
                filePath = "dir/file2",
                isDirectory = false
        )
        val dir1 = LocalFileResponse(
                fileName = "dir1",
                filePath = "dir/dir1",
                isDirectory = true
        )
        val files = listOf(file1, file2, dir1)
        return LocalFileListResponse(
                rootPath = "dir",
                files = files
        )
    }

    @Test
    fun test_getFilesFromDirectory() {
        val path = "dir"
        val files = mockFiles()
        `when`(localFileService.getFilesFromDirectory(path, false))
                .thenReturn(files)

        mockMvcHandler.token = adminToken
        val response = mockMvcHandler.doGet("/api/localfiles/directory?path=$path")
        assertThat(response, allOf(
                hasProperty("status", equalTo(200)),
                responseBody(equalTo(jacksonLocalFileList.write(files).json))
        ))
    }

    @Test
    fun test_getFilesFromDirectory_noPath() {
        val files = mockFiles()
        `when`(localFileService.getFilesFromDirectory(null, false))
                .thenReturn(files)

        mockMvcHandler.token = adminToken
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
        mockMvcHandler.token = token
        val response = mockMvcHandler.doGet("/api/localfiles/directory")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

}
