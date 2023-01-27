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

package io.craigmiller160.videomanagerserver.service.file

import io.craigmiller160.videomanagerserver.dto.LocalFileResponse
import java.io.File
import java.nio.file.Files
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class LocalFileServiceTest {

  companion object {
    private const val TARGET_DIR_NAME = "targetDir"
    private const val HOME_DIR_NAME = "homeDir"
    private const val FILE_1_NAME = "file1"
    private const val FILE_2_NAME = "file2"
    private const val FILE_3_NAME = "file3"
    private const val FILE_4_NAME = "file4"
    private const val FILE_5_NAME = "file5"
    private const val DIR_1_NAME = "dir1"
    private const val DIR_2_NAME = "dir2"
    private const val TEXT = "Hello World"
  }

  private lateinit var targetDir: File
  private lateinit var homeDir: File
  private lateinit var localFileService: LocalFileService

  @BeforeEach
  fun setup() {
    targetDir = Files.createTempDirectory(TARGET_DIR_NAME).toFile()
    Files.write(File(targetDir, FILE_1_NAME).toPath(), TEXT.toByteArray())
    Files.write(File(targetDir, FILE_2_NAME).toPath(), TEXT.toByteArray())
    Files.write(File(targetDir, FILE_3_NAME).toPath(), TEXT.toByteArray())
    File(targetDir, DIR_2_NAME).mkdirs()

    homeDir = Files.createTempDirectory(HOME_DIR_NAME).toFile()
    Files.write(File(homeDir, FILE_4_NAME).toPath(), TEXT.toByteArray())
    Files.write(File(homeDir, FILE_5_NAME).toPath(), TEXT.toByteArray())
    File(homeDir, DIR_1_NAME).mkdirs()

    localFileService = LocalFileService(homeDir.absolutePath)
  }

  @Test
  fun test_getFilesFromDirectory() {
    val files = localFileService.getFilesFromDirectory(targetDir.absolutePath, false)
    assertThat(
      files,
      allOf(
        hasProperty("rootPath", equalTo(targetDir.absolutePath)),
        hasProperty("parentPath", equalTo(targetDir.parentFile.absolutePath)),
        hasProperty(
          "files",
          allOf<List<LocalFileResponse>>(
            hasSize(4),
            containsInAnyOrder<LocalFileResponse>(
              allOf(
                hasProperty("fileName", equalTo(FILE_1_NAME)),
                hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                hasProperty("directory", equalTo(false))),
              allOf(
                hasProperty("fileName", equalTo(FILE_2_NAME)),
                hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                hasProperty("directory", equalTo(false))),
              allOf(
                hasProperty("fileName", equalTo(FILE_3_NAME)),
                hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                hasProperty("directory", equalTo(false))),
              allOf(
                hasProperty("fileName", equalTo(DIR_2_NAME)),
                hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                hasProperty("directory", equalTo(true))))))))
  }

  @Test
  fun test_getFilesFromDirectory_onlyDirectories() {
    val files = localFileService.getFilesFromDirectory(targetDir.absolutePath, true)
    assertThat(
      files,
      allOf(
        hasProperty("rootPath", equalTo(targetDir.absolutePath)),
        hasProperty("parentPath", equalTo(targetDir.parentFile.absolutePath)),
        hasProperty(
          "files",
          allOf<List<LocalFileResponse>>(
            hasSize(1),
            containsInAnyOrder<LocalFileResponse>(
              allOf(
                hasProperty("fileName", equalTo(DIR_2_NAME)),
                hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                hasProperty("directory", equalTo(true))))))))
  }

  @Test
  fun test_getFilesFromDirectory_noPath() {
    val files = localFileService.getFilesFromDirectory(null, false)
    assertThat(
      files,
      allOf(
        hasProperty("rootPath", equalTo(homeDir.absolutePath)),
        hasProperty("parentPath", equalTo(homeDir.parentFile.absolutePath)),
        hasProperty(
          "files",
          allOf<List<LocalFileResponse>>(
            hasSize(3),
            containsInAnyOrder<LocalFileResponse>(
              allOf(
                hasProperty("fileName", equalTo(FILE_4_NAME)),
                hasProperty("filePath", containsString(HOME_DIR_NAME)),
                hasProperty("directory", equalTo(false))),
              allOf(
                hasProperty("fileName", equalTo(FILE_5_NAME)),
                hasProperty("filePath", containsString(HOME_DIR_NAME)),
                hasProperty("directory", equalTo(false))),
              allOf(
                hasProperty("fileName", equalTo(DIR_1_NAME)),
                hasProperty("filePath", containsString(HOME_DIR_NAME)),
                hasProperty("directory", equalTo(true))))))))
  }
}
