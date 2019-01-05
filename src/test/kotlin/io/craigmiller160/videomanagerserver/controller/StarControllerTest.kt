package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.service.StarService
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.Optional

class StarControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var mockMvcHandler: MockMvcHandler

    @Mock
    private lateinit var starService: StarService

    private lateinit var starController: StarController

    private lateinit var jacksonStarList: JacksonTester<List<Star>>
    private lateinit var jacksonStar: JacksonTester<Star>

    private lateinit var starNoId: Star
    private lateinit var star1: Star
    private lateinit var star2: Star
    private lateinit var star3: Star
    private lateinit var starList: List<Star>

    @Before
    fun setup() {
        starNoId = Star(starName = "NoId")
        star1 = Star(1, "FirstStar")
        star2 = Star(2, "SecondStar")
        star3 = Star(3, "ThirdStar")
        starList = listOf(star1, star2, star3)

        MockitoAnnotations.initMocks(this)
        JacksonTester.initFields(this, ObjectMapper())

        starController = StarController(starService)
        mockMvc = MockMvcBuilders.standaloneSetup(starController).build()
        mockMvcHandler = MockMvcHandler(mockMvc)
    }

    @Test
    fun testGetAllStars() {
        `when`(starService.getAllStars())
                .thenReturn(starList)
                .thenReturn(listOf())

        var response = mockMvcHandler.doGet("/stars")
        assertOkResponse(response, jacksonStarList.write(starList).json)

        response = mockMvcHandler.doGet("/stars")
        assertNoContentResponse(response)
    }

    @Test
    fun testGetStar() {
        `when`(starService.getStar(1))
                .thenReturn(Optional.of(star1))
        `when`(starService.getStar(5))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doGet("/stars/1")
        assertOkResponse(response, jacksonStar.write(star1).json)

        response = mockMvcHandler.doGet("/stars/5")
        assertNoContentResponse(response)
    }

    @Test
    fun testAddStar() {
        val starWithId = starNoId.copy(starId = 1)
        `when`(starService.addStar(starNoId))
                .thenReturn(starWithId)

        val response = mockMvcHandler.doPost("/stars", jacksonStar.write(starNoId).json)
        assertOkResponse(response, jacksonStar.write(starWithId).json)
    }

    @Test
    fun testUpdateStar() {
        val updatedCategory = star2.copy(starId = 1)
        `when`(starService.updateStar(1, star2))
                .thenReturn(Optional.of(updatedCategory))
        `when`(starService.updateStar(5, star3))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doPut("/stars/1", jacksonStar.write(star2).json)
        assertOkResponse(response, jacksonStar.write(updatedCategory).json)

        response = mockMvcHandler.doPut("/stars/5", jacksonStar.write(star3).json)
        assertNoContentResponse(response)
    }

    @Test
    fun testDeleteStar() {
        `when`(starService.deleteStar(1))
                .thenReturn(Optional.of(star1))
                .thenReturn(Optional.empty())

        var response = mockMvcHandler.doDelete("/stars/1")
        assertOkResponse(response, jacksonStar.write(star1).json)

        response = mockMvcHandler.doDelete("/stars/5")
        assertNoContentResponse(response)
    }

}