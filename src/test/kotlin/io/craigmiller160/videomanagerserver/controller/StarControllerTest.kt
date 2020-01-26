package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.craigmiller160.videomanagerserver.dto.AppUser
import io.craigmiller160.videomanagerserver.dto.Role
import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.security.ROLE_EDIT
import io.craigmiller160.videomanagerserver.security.tokenprovider.JwtTokenProvider
import io.craigmiller160.videomanagerserver.service.StarService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.boot.test.mock.mockito.MockBean
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
class StarControllerTest : AbstractControllerTest() {

    private lateinit var mockMvcHandler: MockMvcHandler

    @MockBean
    private lateinit var starService: StarService

    @Autowired
    private lateinit var starController: StarController

    private lateinit var jacksonStarList: JacksonTester<List<Star>>
    private lateinit var jacksonStar: JacksonTester<Star>

    private lateinit var starNoId: Star
    private lateinit var star1: Star
    private lateinit var star2: Star
    private lateinit var star3: Star
    private lateinit var starList: List<Star>

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Before
    fun setup() {
        starNoId = Star(starName = "NoId")
        star1 = Star(1, "FirstStar")
        star2 = Star(2, "SecondStar")
        star3 = Star(3, "ThirdStar")
        starList = listOf(star1, star2, star3)

        mockMvcHandler = buildMockMvcHandler()
        JacksonTester.initFields(this, ObjectMapper())
    }

    @Test
    fun testGetAllStars() {
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        `when`(starService.getAllStars())
                .thenReturn(starList)
                .thenReturn(listOf())

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
        mockMvcHandler.token = jwtTokenProvider.createToken(AppUser(userName = "userName"))
        `when`(starService.getStar(1))
                .thenReturn(Optional.of(star1))
        `when`(starService.getStar(5))
                .thenReturn(Optional.empty())

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
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_EDIT))
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val starWithId = starNoId.copy(starId = 1)
        `when`(starService.addStar(starNoId))
                .thenReturn(starWithId)

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
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doPost("/api/stars", jacksonStar.write(starNoId).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun testUpdateStar() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_EDIT))
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        val updatedStar = star2.copy(starId = 1)
        `when`(starService.updateStar(1, star2))
                .thenReturn(Optional.of(updatedStar))
        `when`(starService.updateStar(5, star3))
                .thenReturn(Optional.empty())

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
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doPut("/api/stars/1", jacksonStar.write(star2).json)
        assertThat(response, hasProperty("status", equalTo(403)))
    }

    @Test
    fun testDeleteStar() {
        val user = AppUser(
                userName = "userName",
                roles = listOf(Role(name = ROLE_EDIT))
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)
        `when`(starService.deleteStar(1))
                .thenReturn(Optional.of(star1))
                .thenReturn(Optional.empty())

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
        val user = AppUser(
                userName = "userName"
        )
        mockMvcHandler.token = jwtTokenProvider.createToken(user)

        val response = mockMvcHandler.doDelete("/api/stars/1")
        assertThat(response, hasProperty("status", equalTo(403)))
    }

}
