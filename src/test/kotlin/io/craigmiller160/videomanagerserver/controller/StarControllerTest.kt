package io.craigmiller160.videomanagerserver.controller

import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.service.StarService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.web.servlet.MockMvc

class StarControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Mock
    private lateinit var starService: StarService

    private lateinit var seriesController: StarController

    private lateinit var jacksonStarList: JacksonTester<List<Star>>
    private lateinit var jacksonStar: JacksonTester<Star>

    private lateinit var starNoId: Star
    private lateinit var star1: Star
    private lateinit var star2: Star
    private lateinit var star3: Star
    private lateinit var starList: List<Star>

    @Before
    fun setup() {
        TODO("Finish this")
    }

    @Test
    fun testGetAllStars() {
        TODO("Finish this")
    }

    @Test
    fun testGetStar() {
        TODO("Finish this")
    }

    @Test
    fun testAddStar() {
        TODO("Finish this")
    }

    @Test
    fun testUpdateStar() {
        TODO("Finish this")
    }

    @Test
    fun testDeleteStar() {
        TODO("Finish this")
    }

}