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

import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.entity.AppUser
import io.craigmiller160.videomanagerserver.entity.Role
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.service.videofile.SeriesService
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Assert.assertThat
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

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class SeriesControllerTest : AbstractControllerTest() {

    @MockBean
    private lateinit var seriesService: SeriesService

    @Autowired
    private lateinit var seriesController: SeriesController

    private lateinit var jacksonSeriesList: JacksonTester<List<SeriesPayload>>
    private lateinit var jacksonSeries: JacksonTester<SeriesPayload>

    private lateinit var seriesNoId: SeriesPayload
    private lateinit var series1: SeriesPayload
    private lateinit var series2: SeriesPayload
    private lateinit var series3: SeriesPayload
    private lateinit var seriesList: List<SeriesPayload>

    @Before
    override fun setup() {
        super.setup()
        seriesNoId = SeriesPayload(seriesName = "NoId")
        series1 = SeriesPayload(1, "FirstSeries")
        series2 = SeriesPayload(2, "SecondSeries")
        series3 = SeriesPayload(3, "ThirdSeries")
        seriesList = listOf(series1, series2, series3)
    }

    @Test
    fun testGetAllSeries() {
        mockMvcHandler.token = token
        `when`(seriesService.getAllSeries())
                .thenReturn(seriesList)
                .thenReturn(listOf())

        var response = mockMvcHandler.doGet("/api/series")
        assertOkResponse(response, jacksonSeriesList.write(seriesList).json)

        response = mockMvcHandler.doGet("/api/series")
        assertNoContentResponse(response)
    }

    @Test
    fun test_getAllSeries_unauthorized() {
        val response = mockMvcHandler.doGet("/api/series")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun testGetSeries() {
        mockMvcHandler.token = token
        `when`(seriesService.getSeries(1))
                .thenReturn(series1)
        `when`(seriesService.getSeries(5))
                .thenReturn(null)

        var response = mockMvcHandler.doGet("/api/series/1")
        assertOkResponse(response, jacksonSeries.write(series1).json)

        response = mockMvcHandler.doGet("/api/series/5")
        assertNoContentResponse(response)
    }

    @Test
    fun test_getSeries_unauthorized() {
        val response = mockMvcHandler.doGet("/api/series/1")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun testAddSeries() {
        mockMvcHandler.token = editToken
        val seriesWithId = seriesNoId.copy(seriesId = 1)
        `when`(seriesService.addSeries(seriesNoId))
                .thenReturn(seriesWithId)

        val response = mockMvcHandler.doPost("/api/series", jacksonSeries.write(seriesNoId).json)
        assertOkResponse(response, jacksonSeries.write(seriesWithId).json)
    }

    @Test
    fun test_addSeries_unauthorized() {
        val response = mockMvcHandler.doPost("/api/series", jacksonSeries.write(seriesNoId).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_addSeries_missingRole() {
        mockMvcHandler.token = token

        val response = mockMvcHandler.doPost("/api/series", jacksonSeries.write(seriesNoId).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun testUpdateSeries() {
        mockMvcHandler.token = editToken
        val updatedSeries = series2.copy(seriesId = 1)
        `when`(seriesService.updateSeries(1, series2))
                .thenReturn(updatedSeries)
        `when`(seriesService.updateSeries(5, series3))
                .thenReturn(null)

        var response = mockMvcHandler.doPut("/api/series/1", jacksonSeries.write(series2).json)
        assertOkResponse(response, jacksonSeries.write(updatedSeries).json)

        response = mockMvcHandler.doPut("/api/series/5", jacksonSeries.write(series3).json)
        assertNoContentResponse(response)
    }

    @Test
    fun test_updateSeries_unauthorized() {
        val response = mockMvcHandler.doPut("/api/series/1", jacksonSeries.write(series2).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_updateSeries_missingRole() {
        mockMvcHandler.token = token

        val response = mockMvcHandler.doPut("/api/series/1", jacksonSeries.write(series2).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun testDeleteSeries() {
        mockMvcHandler.token = editToken
        `when`(seriesService.deleteSeries(1))
                .thenReturn(series1)
                .thenReturn(null)

        var response = mockMvcHandler.doDelete("/api/series/1")
        assertOkResponse(response, jacksonSeries.write(series1).json)

        response = mockMvcHandler.doDelete("/api/series/5")
        assertNoContentResponse(response)
    }

    @Test
    fun test_deleteSeries_unauthorized() {
        val response = mockMvcHandler.doDelete("/api/series/1")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun test_deleteSeries_missingRole() {
        mockMvcHandler.token = token

        val response = mockMvcHandler.doDelete("/api/series/1")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

}
