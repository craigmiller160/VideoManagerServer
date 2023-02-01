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

package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.entity.VideoFile
import io.craigmiller160.videomanagerserver.test_util.AuthenticationConfig
import io.craigmiller160.videomanagerserver.test_util.DbTestUtils
import kotlin.test.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class FileStarRepositoryIntegrationTest {

  companion object {
    private const val STAR_NAME = "starName"
    private const val STAR_2_NAME = "star2Name"
    private const val FILE_NAME = "fileName"
    private const val FILE_2_NAME = "file2Name"
  }

  @Autowired private lateinit var starRepo: StarRepository
  @Autowired private lateinit var fileStarRepo: FileStarRepository
  @Autowired private lateinit var videoFileRepo: VideoFileRepository
  @Autowired private lateinit var dbTestUtils: DbTestUtils
  @MockBean private lateinit var authConfig: AuthenticationConfig

  private var fileId = 0L
  private var starId = 0L

  @BeforeEach
  fun setup() {
    val star = Star(starName = STAR_NAME)
    val file = VideoFile(fileName = FILE_NAME)
    file.stars.add(star)
    videoFileRepo.save(file)

    fileId = file.fileId
    starId = star.starId

    val star2 = Star(starName = STAR_2_NAME)
    val file2 = VideoFile(fileName = FILE_2_NAME)
    file2.stars.add(star2)
    videoFileRepo.save(file2)
  }

  private fun validateRecordsExist() {
    assertEquals(2, videoFileRepo.count())
    assertEquals(2, starRepo.count())
    assertEquals(2, fileStarRepo.count())
  }

  @AfterEach
  fun clean() {
    dbTestUtils.cleanDb()
  }

  @Test
  fun test_deleteAllByStarId() {
    validateRecordsExist()
    fileStarRepo.deleteAllByStarId(starId)
    val results = fileStarRepo.findAll()
    assertThat(results, allOf(hasSize(1), contains(hasProperty("starId", not(equalTo(starId))))))
  }

  @Test
  fun test_deleteAllByFileId() {
    validateRecordsExist()
    fileStarRepo.deleteAllByFileId(fileId)
    val results = fileStarRepo.findAll()
    assertThat(results, allOf(hasSize(1), contains(hasProperty("fileId", not(equalTo(fileId))))))
  }
}
