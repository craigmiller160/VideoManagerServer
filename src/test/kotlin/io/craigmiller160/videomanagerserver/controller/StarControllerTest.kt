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

import io.craigmiller160.videomanagerserver.dto.StarPayload
import io.craigmiller160.videomanagerserver.service.videofile.StarService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration

@ExtendWith(SpringExtension::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class StarControllerTest : AbstractControllerTest() {

  @MockBean private lateinit var starService: StarService

  @Autowired private lateinit var starController: StarController

  private lateinit var jacksonStarList: JacksonTester<List<StarPayload>>
  private lateinit var jacksonStar: JacksonTester<StarPayload>

  private lateinit var starNoId: StarPayload
  private lateinit var star1: StarPayload
  private lateinit var star2: StarPayload
  private lateinit var star3: StarPayload
  private lateinit var starList: List<StarPayload>

  @BeforeEach
  override fun setup() {
    super.setup()
    starNoId = StarPayload(starName = "NoId")
    star1 = StarPayload(1, "FirstStar")
    star2 = StarPayload(2, "SecondStar")
    star3 = StarPayload(3, "ThirdStar")
    starList = listOf(star1, star2, star3)
  }

  @Test
  fun testGetAllStars() {
    mockMvcHandler.token = token
    `when`(starService.getAllStars()).thenReturn(starList).thenReturn(listOf())

    var response = mockMvcHandler.doGet("/api/stars")
    assertOkResponse(response, jacksonStarList.write(starList).json)

    response = mockMvcHandler.doGet("/api/stars")
    assertNoContentResponse(response)
  }

  @Test
  fun test_getAllStars_unauthorized() {
    val response = mockMvcHandler.doGet("/api/stars")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testGetStar() {
    mockMvcHandler.token = token
    `when`(starService.getStar(1)).thenReturn(star1)
    `when`(starService.getStar(5)).thenReturn(null)

    var response = mockMvcHandler.doGet("/api/stars/1")
    assertOkResponse(response, jacksonStar.write(star1).json)

    response = mockMvcHandler.doGet("/api/stars/5")
    assertNoContentResponse(response)
  }

  @Test
  fun test_getStar_unauthorized() {
    val response = mockMvcHandler.doGet("/api/stars/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun testAddStar() {
    mockMvcHandler.token = editToken
    val starWithId = starNoId.copy(starId = 1)
    `when`(starService.addStar(starNoId)).thenReturn(starWithId)

    val response = mockMvcHandler.doPost("/api/stars", jacksonStar.write(starNoId).json)
    assertOkResponse(response, jacksonStar.write(starWithId).json)
  }

  @Test
  fun test_addStar_unauthorized() {
    val response = mockMvcHandler.doPost("/api/stars", jacksonStar.write(starNoId).json)
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_addStar_missingRole() {
    mockMvcHandler.token = token

    val response = mockMvcHandler.doPost("/api/stars", jacksonStar.write(starNoId).json)
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun testUpdateStar() {
    mockMvcHandler.token = editToken
    val updatedStar = star2.copy(starId = 1)
    `when`(starService.updateStar(1, star2)).thenReturn(updatedStar)
    `when`(starService.updateStar(5, star3)).thenReturn(null)

    var response = mockMvcHandler.doPut("/api/stars/1", jacksonStar.write(star2).json)
    assertOkResponse(response, jacksonStar.write(updatedStar).json)

    response = mockMvcHandler.doPut("/api/stars/5", jacksonStar.write(star3).json)
    assertNoContentResponse(response)
  }

  @Test
  fun test_updateStar_unauthorized() {
    val response = mockMvcHandler.doPut("/api/stars/1", jacksonStar.write(star2).json)
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_updateStar_missingRole() {
    mockMvcHandler.token = token

    val response = mockMvcHandler.doPut("/api/stars/1", jacksonStar.write(star2).json)
    assertThat(response, hasProperty("status", equalTo(403)))
  }

  @Test
  fun testDeleteStar() {
    mockMvcHandler.token = editToken
    `when`(starService.deleteStar(1)).thenReturn(star1).thenReturn(null)

    var response = mockMvcHandler.doDelete("/api/stars/1")
    assertOkResponse(response, jacksonStar.write(star1).json)

    response = mockMvcHandler.doDelete("/api/stars/5")
    assertNoContentResponse(response)
  }

  @Test
  fun test_deleteStar_unauthorized() {
    val response = mockMvcHandler.doDelete("/api/stars/1")
    assertThat(response, hasProperty("status", equalTo(401)))
  }

  @Test
  fun test_deleteStar_missingRole() {
    mockMvcHandler.token = token

    val response = mockMvcHandler.doDelete("/api/stars/1")
    assertThat(response, hasProperty("status", equalTo(403)))
  }
}
