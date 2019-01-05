package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.service.SeriesService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.Optional

class SeriesControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Mock
    private lateinit var seriesService: SeriesService

    private lateinit var seriesController: SeriesController

    private lateinit var jacksonSeriesList: JacksonTester<List<Series>>
    private lateinit var jacksonSeries: JacksonTester<Series>

    private lateinit var seriesNoId: Series
    private lateinit var series1: Series
    private lateinit var series2: Series
    private lateinit var series3: Series
    private lateinit var seriesList: List<Series>

    @Before
    fun setup() {
        seriesNoId = Series(seriesName = "NoId")
        series1 = Series(1, "FirstSeries")
        series2 = Series(2, "SecondSeries")
        series3 = Series(3, "ThirdSeries")
        seriesList = listOf(series1, series2, series3)

        MockitoAnnotations.initMocks(this)
        JacksonTester.initFields(this, ObjectMapper())

        seriesController = SeriesController(seriesService)
        mockMvc = MockMvcBuilders.standaloneSetup(seriesController).build()
        mockMvcHandler = MockMvcHandler(mockMvc)
    }

    @Test
    fun testGetAllSeries() {
        `when`(seriesService.getAllSeries())
                .thenReturn(seriesList)
                .thenReturn(listOf())

        var response = mockMvcHandler.doGet("/series")
        assertOkResponse(response, jacksonSeriesList.write(seriesList).json)

        response = mockMvcHandler.doGet("/series")
        assertNoContentResponse(response)
    }

    @Test
    fun testGetSeries() {
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
    fun testAddSeries() {
        val seriesWithId = seriesNoId.copy(seriesId = 1)
        `when`(seriesService.addSeries(seriesNoId))
                .thenReturn(seriesWithId)

        val response = mockMvcHandler.doPost("/series", jacksonSeries.write(seriesNoId).json)
        assertOkResponse(response, jacksonSeries.write(seriesWithId).json)
    }

    @Test
    fun testUpdateSeries() {
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
    fun testDeleteSeries() {
        `when`(seriesService.deleteSeries(1))
                .thenReturn(Optional.of(series1))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doDelete("/series/1")
        assertOkResponse(response, jacksonSeries.write(series1).json)

        response = mockMvcHandler.doDelete("/series/5")
        assertNoContentResponse(response)
    }

}