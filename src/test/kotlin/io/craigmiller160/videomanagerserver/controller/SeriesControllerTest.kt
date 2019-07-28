package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.security.jwt.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.SeriesService
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.Optional

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class SeriesControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Mock
    private lateinit var seriesService: SeriesService

    @Autowired
    private lateinit var seriesController: SeriesController

    private lateinit var jacksonSeriesList: JacksonTester<List<Series>>
    private lateinit var jacksonSeries: JacksonTester<Series>

    private lateinit var seriesNoId: Series
    private lateinit var series1: Series
    private lateinit var series2: Series
    private lateinit var series3: Series
    private lateinit var seriesList: List<Series>

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Before
    fun setup() {
        seriesNoId = Series(seriesName = "NoId")
        series1 = Series(1, "FirstSeries")
        series2 = Series(2, "SecondSeries")
        series3 = Series(3, "ThirdSeries")
        seriesList = listOf(series1, series2, series3)

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        mockMvcHandler = MockMvcHandler(mockMvc)

        MockitoAnnotations.initMocks(this)
        JacksonTester.initFields(this, ObjectMapper())
        ReflectionTestUtils.setField(seriesController, "seriesService", seriesService)
    }

    @Test
    fun testGetAllSeries() {
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        `when`(seriesService.getAllSeries())
                .thenReturn(seriesList)
                .thenReturn(listOf())

        var response = mockMvcHandler.doGet("/series")
        assertOkResponse(response, jacksonSeriesList.write(seriesList).json)

        response = mockMvcHandler.doGet("/series")
        assertNoContentResponse(response)
    }

    @Test
    fun test_getAllSeries_unauthorized() {
        val response = mockMvcHandler.doGet("/series")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun testGetSeries() {
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        `when`(seriesService.getSeries(1))
                .thenReturn(Optional.of(series1))
        `when`(seriesService.getSeries(5))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doGet("/series/1")
        assertOkResponse(response, jacksonSeries.write(series1).json)

        response = mockMvcHandler.doGet("/series/5")
        assertNoContentResponse(response)
    }

    @Test
    fun test_getSeries_unauthorized() {
        val response = mockMvcHandler.doGet("/series/1")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun testAddSeries() {
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        val seriesWithId = seriesNoId.copy(seriesId = 1)
        `when`(seriesService.addSeries(seriesNoId))
                .thenReturn(seriesWithId)

        val response = mockMvcHandler.doPost("/series", jacksonSeries.write(seriesNoId).json)
        assertOkResponse(response, jacksonSeries.write(seriesWithId).json)
    }

    @Test
    fun test_addSeries_unauthorized() {
        val response = mockMvcHandler.doPost("/series", jacksonSeries.write(seriesNoId).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun testUpdateSeries() {
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        val updatedSeries = series2.copy(seriesId = 1)
        `when`(seriesService.updateSeries(1, series2))
                .thenReturn(Optional.of(updatedSeries))
        `when`(seriesService.updateSeries(5, series3))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doPut("/series/1", jacksonSeries.write(series2).json)
        assertOkResponse(response, jacksonSeries.write(updatedSeries).json)

        response = mockMvcHandler.doPut("/series/5", jacksonSeries.write(series3).json)
        assertNoContentResponse(response)
    }

    @Test
    fun test_updateSeries_unauthorized() {
        val response = mockMvcHandler.doPut("/series/1", jacksonSeries.write(series2).json)
        assertThat(response, hasProperty("status", equalTo(401)))
    }

    @Test
    fun testDeleteSeries() {
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        `when`(seriesService.deleteSeries(1))
                .thenReturn(Optional.of(series1))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doDelete("/series/1")
        assertOkResponse(response, jacksonSeries.write(series1).json)

        response = mockMvcHandler.doDelete("/series/5")
        assertNoContentResponse(response)
    }

    @Test
    fun test_deleteSeries_unauthorized() {
        val response = mockMvcHandler.doDelete("/series/1")
        assertThat(response, hasProperty("status", equalTo(401)))
    }

}