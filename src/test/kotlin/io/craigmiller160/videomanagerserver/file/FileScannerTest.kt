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

package io.craigmiller160.videomanagerserver.file

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.config.VideoConfiguration
import io.craigmiller160.videomanagerserver.dto.SettingsPayload
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.exception.InvalidSettingException
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.service.settings.SettingsService
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class FileScannerTest {

  companion object {

    private lateinit var rootPath: String

    @BeforeAll
    @JvmStatic
    fun findDirs() {
      val url =
        FileScannerTest::class
          .java
          .classLoader
          .getResource("io/craigmiller160/videomanagerserver/file/")
      rootPath = url.toURI().path
    }
  }

  private lateinit var videoConfig: VideoConfiguration

  @Mock private lateinit var videoFileRepo: VideoFileRepository

  private lateinit var fileScanner: FileScanner

  @Mock private lateinit var settingsService: SettingsService

  @BeforeEach
  fun setup() {
    MockitoAnnotations.initMocks(this)

    videoConfig = VideoConfiguration()
    videoConfig.fileExts = "txt,csv"
    fileScanner = FileScanner(videoConfig, videoFileRepo, settingsService)
  }

  @Test
  fun test_scanForFiles_noRootDir() {
    `when`(settingsService.getOrCreateSettings()).thenReturn(SettingsPayload())

    val done = AtomicBoolean(false)

    assertThrows<InvalidSettingException> { fileScanner.scanForFiles { done.set(true) } }
  }

  @Test
  fun test_scanForFiles() {
    val settings = SettingsPayload(rootDir = rootPath)
    `when`(settingsService.getOrCreateSettings()).thenReturn(settings)

    runBlocking {
      val start = LocalDateTime.now()

      val done = AtomicBoolean(false)

      val job = fileScanner.scanForFiles { done.set(true) }

      withTimeout(10_000) { job.join() }

      assertTrue(done.get())

      // This insanity is from needing a separate library to handle kotlin null safety and some
      // mocking methods
      val argumentCaptor =
        argumentCaptor<VideoFile>().apply { verify(videoFileRepo, times(4)).save(capture()) }

      verify(videoFileRepo, times(1)).setOldFilesInactive(any())

      val allValues = argumentCaptor.allValues
      val allValuesSorted = allValues.sortedBy { it.fileName }
      assertThat(
        allValuesSorted,
        allOf(
          hasSize(equalTo(4)),
          contains(
            allOf(
              hasProperty("fileName", `is`("myFile.txt")),
              hasProperty("fileAdded", greaterThan(start)),
              hasProperty("active", equalTo(true))),
            allOf(
              hasProperty("fileName", `is`("myFile2.txt")),
              hasProperty("fileAdded", greaterThan(start)),
              hasProperty("active", equalTo(true))),
            allOf(
              hasProperty("fileName", `is`("otherExt.csv")),
              hasProperty("fileAdded", greaterThan(start)),
              hasProperty("active", equalTo(true))),
            allOf(
              hasProperty("fileName", `is`("subdir/subDirFile.txt")),
              hasProperty("fileAdded", greaterThan(start)),
              hasProperty("active", equalTo(true))))))
    }
  }
}
