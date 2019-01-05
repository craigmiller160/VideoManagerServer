package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.service.SeriesService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.web.servlet.MockMvc

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
        TODO("Finish this")
    }

    @Test
    fun testGetAllSeries() {
        TODO("Finish this")
    }

    @Test
    fun testGetSeries() {
        TODO("Finish this")
    }

    @Test
    fun testUpdateSeries() {
        TODO("Finish this")
    }

    @Test
    fun testDeleteSeries() {
        TODO("Finish this")
    }

}