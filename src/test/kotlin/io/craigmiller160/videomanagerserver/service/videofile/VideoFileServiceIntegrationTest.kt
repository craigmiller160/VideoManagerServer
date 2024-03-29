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

package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.*
import io.craigmiller160.videomanagerserver.entity.Category
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.entity.sort.VideoFileSortBy
import io.craigmiller160.videomanagerserver.mapper.VMModelMapper
import io.craigmiller160.videomanagerserver.repository.VideoFileRepository
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class VideoFileServiceIntegrationTest {

  companion object {
    private const val FILE_NAME = "MyFile"
    private const val FILE_DISPLAY_NAME = "MyDisplayFile"

    private const val FILE_NAME_2 = "MyFile3"
    private const val FILE_DISPLAY_NAME_2 = "MyDisplayFile2"

    private const val FILE_NAME_3 = "MyFile2"

    private const val FILE_NAME_4 = "MyFile4"

    private val NOW_TIMESTAMP = LocalDateTime.now()
  }

  @Autowired private lateinit var dbTestUtils: DbTestUtils
  @Autowired private lateinit var videoFileService: VideoFileService
  @Autowired private lateinit var videoFileRepo: VideoFileRepository
  @Autowired private lateinit var modelMapper: VMModelMapper

  private lateinit var file1: VideoFilePayload
  private lateinit var file2: VideoFilePayload
  private lateinit var file3: VideoFilePayload
  private lateinit var file4: VideoFilePayload

  @BeforeEach
  fun setup() {
    val category = CategoryPayload(categoryName = "MyCategory")
    val series = SeriesPayload(seriesName = "MySeries")
    val star = StarPayload(starName = "MyStar")

    file1 =
      VideoFilePayload(fileName = FILE_NAME, displayName = FILE_DISPLAY_NAME).apply {
        categories.add(category)
        this.series.add(series)
        stars.add(star)
      }
    file1 = videoFileService.addVideoFile(file1)
    videoFileRepo.findById(file1.fileId).ifPresent { videoFile ->
      videoFile.lastScanTimestamp = NOW_TIMESTAMP
      videoFileRepo.save(videoFile)
    }

    file2 = VideoFilePayload(fileName = FILE_NAME_2, displayName = FILE_DISPLAY_NAME_2)
    file2 = videoFileService.addVideoFile(file2)

    file3 = VideoFilePayload(fileName = FILE_NAME_3, displayName = FILE_DISPLAY_NAME_2)
    file3 = videoFileService.addVideoFile(file3)

    file4 = VideoFilePayload(fileName = FILE_NAME_4)
  }

  @AfterEach
  fun clean() {
    dbTestUtils.cleanDb()
  }

  @Test
  fun test_searchForVideos_sortAsc() {
    val search = VideoSearchRequest(sortBy = VideoFileSortBy.NAME, sortDir = Sort.Direction.ASC)
    val results = videoFileService.searchForVideos(search)
    assertThat(
      results,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", Matchers.contains(file1, file3, file2))))
  }

  @Test
  fun test_searchForVideos_lastViewed_sortDesc() {
    val search =
      VideoSearchRequest(sortBy = VideoFileSortBy.LAST_VIEWED, sortDir = Sort.Direction.ASC)
    val fileEntity1 = videoFileRepo.findById(file1.fileId).get()
    val fileEntity2 = videoFileRepo.findById(file2.fileId).get()
    fileEntity1.lastViewed = LocalDateTime.now().minusDays(1)
    fileEntity2.lastViewed = LocalDateTime.now().minusDays(2)
    videoFileRepo.save(fileEntity1)
    videoFileRepo.save(fileEntity2)

    val results = videoFileService.searchForVideos(search)
    assertThat(
      results,
      allOf(
        hasProperty("totalFiles", Matchers.equalTo(3L)),
        hasProperty("filesPerPage", Matchers.equalTo(10)),
        hasProperty("currentPage", Matchers.equalTo(0)),
        hasProperty(
          "videoList",
          Matchers.contains(
            modelMapper.map(fileEntity2, VideoFilePayload::class.java),
            modelMapper.map(fileEntity1, VideoFilePayload::class.java),
            file3))))
  }

  @Test
  fun test_searchForVideos_lastViewed_sortAsc() {
    val search =
      VideoSearchRequest(sortBy = VideoFileSortBy.LAST_VIEWED, sortDir = Sort.Direction.DESC)
    val fileEntity1 = videoFileRepo.findById(file1.fileId).get()
    val fileEntity2 = videoFileRepo.findById(file2.fileId).get()
    fileEntity1.lastViewed = LocalDateTime.now().minusDays(1)
    fileEntity2.lastViewed = LocalDateTime.now().minusDays(2)
    videoFileRepo.save(fileEntity1)
    videoFileRepo.save(fileEntity2)

    val results = videoFileService.searchForVideos(search)
    assertThat(
      results,
      allOf(
        hasProperty("totalFiles", Matchers.equalTo(3L)),
        hasProperty("filesPerPage", Matchers.equalTo(10)),
        hasProperty("currentPage", Matchers.equalTo(0)),
        hasProperty(
          "videoList",
          Matchers.contains(
            modelMapper.map(fileEntity1, VideoFilePayload::class.java),
            modelMapper.map(fileEntity2, VideoFilePayload::class.java),
            file3))))
  }

  @Test
  fun test_searchForVideos_sortDesc() {
    val search = VideoSearchRequest(sortBy = VideoFileSortBy.NAME, sortDir = Sort.Direction.DESC)
    val results = videoFileService.searchForVideos(search)
    assertThat(
      results,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", Matchers.contains(file2, file3, file1))))
  }

  @Test
  fun test_updateVideoFile_removeJoin() {
    val file = videoFileService.getVideoFile(1L)
    file?.categories?.clear()
    videoFileService.updateVideoFile(1L, file!!)

    val result = videoFileService.getVideoFile(1L)
    assertEquals(0, result?.categories?.size)
    assertEquals(1, result?.series?.size)
    assertEquals(1, result?.stars?.size)
  }

  @Test
  fun test_updateVideoFile_addJoin() {
    val file = videoFileService.getVideoFile(1L)
    file?.categories?.add(CategoryPayload(categoryName = "NewCat"))
    videoFileService.updateVideoFile(1L, file!!)

    val result = videoFileService.getVideoFile(1L)
    assertEquals(2, result?.categories?.size)
    assertEquals(1, result?.series?.size)
    assertEquals(1, result?.stars?.size)
  }

  @Test
  fun test_searchForVideos_noCriteria() {
    val search = VideoSearchRequest()
    val result = videoFileService.searchForVideos(search)
    assertThat(
      result,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", containsInAnyOrder(file1, file2, file3))))
  }

  @Test
  fun test_searchForVideos_allCriteria() {
    val search = VideoSearchRequest("File", 1, 1, 1)
    val result = videoFileService.searchForVideos(search)
    assertThat(
      result,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", containsInAnyOrder(file1))))
  }

  @Test
  fun test_searchForVideos_onlyText() {
    val search = VideoSearchRequest("File")
    val result = videoFileService.searchForVideos(search)
    assertThat(
      result,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", Matchers.containsInAnyOrder(file1, file2, file3))))
  }

  @Test
  fun test_searchForVideos_onlyCategory() {
    val search = VideoSearchRequest(categoryId = 1)
    val result = videoFileService.searchForVideos(search)
    assertThat(
      result,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", containsInAnyOrder(file1))))
  }

  @Test
  fun test_searchForVideos_onlySeries() {
    val search = VideoSearchRequest(seriesId = 1)
    val result = videoFileService.searchForVideos(search)
    assertThat(
      result,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", containsInAnyOrder(file1))))
  }

  @Test
  fun test_searchForVideos_onlyStar() {
    val search = VideoSearchRequest(starId = 1)
    val result = videoFileService.searchForVideos(search)
    assertThat(
      result,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(1L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", containsInAnyOrder(file1))))
  }

  @Test
  fun test_searchForVideos_caseInsensitive() {
    val search = VideoSearchRequest(searchText = "FILE")
    val result = videoFileService.searchForVideos(search)
    assertThat(
      result,
      Matchers.allOf(
        Matchers.hasProperty("totalFiles", Matchers.equalTo(3L)),
        Matchers.hasProperty("filesPerPage", Matchers.equalTo(10)),
        Matchers.hasProperty("currentPage", Matchers.equalTo(0)),
        Matchers.hasProperty("videoList", Matchers.containsInAnyOrder(file1, file2, file3))))
  }

  @Test
  fun test_updateVideoFile_preserveDbFields() {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    val newName = "NewName"
    val request =
      file1.copy(
        fileName = newName, categories = mutableSetOf(), viewCount = 10, lastViewed = NOW_TIMESTAMP)

    videoFileService.updateVideoFile(file1.fileId, request)
    val dbFile: VideoFile = videoFileRepo.findById(file1.fileId).get()
    assertThat(
      dbFile,
      allOf(
        hasProperty("fileName", equalTo(newName)),
        hasProperty("active", equalTo(true)),
        hasProperty("categories", hasSize<MutableSet<Category>>(0))))

    val actualLastScanTimestamp = dbFile.lastScanTimestamp.format(formatter)
    val expectedLastScanTimestamp = NOW_TIMESTAMP.format(formatter)
    assertEquals(expectedLastScanTimestamp, actualLastScanTimestamp)

    // TODO in the future add tests for viewCount & lastViewed here, once the timestamp bug is
    // worked out in H2 tests
  }
}
