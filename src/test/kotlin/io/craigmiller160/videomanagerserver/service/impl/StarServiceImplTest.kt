package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.repository.StarRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.isA
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.Sort
import java.util.Optional

class StarServiceImplTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"

        private val expectedStars = listOf(
                Star(starId = 1, starName = FIRST_NAME),
                Star(starId = 2, starName = SECOND_NAME)
        )

    }

    private lateinit var starService: StarServiceImpl

    @Mock
    private lateinit var starRepo: StarRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        starService = StarServiceImpl(starRepo)
    }

    @Test
    fun testGetAllStars() {
        `when`(starRepo.findAll(isA(Sort::class.java)))
                .thenReturn(expectedStars)

        val actualStars = starService.getAllStars()
        assertNotNull(actualStars)
        assertEquals(expectedStars.size, actualStars.size)
        assertEquals(expectedStars, actualStars)
    }

    @Test
    fun testGetStar() {
        `when`(starRepo.findById(1))
                .thenReturn(Optional.of(expectedStars[0]))
        `when`(starRepo.findById(2))
                .thenReturn(Optional.of(expectedStars[1]))

        var actualStar = starService.getStar(1)
        assertTrue(actualStar.isPresent)
        assertEquals(expectedStars[0], actualStar.get())

        actualStar = starService.getStar(2)
        assertTrue(actualStar.isPresent)
        assertEquals(expectedStars[1], actualStar.get())

        actualStar = starService.getStar(3)
        assertFalse(actualStar.isPresent)
    }

    @Test
    fun testAddStar() {
        val newStar = Star(starName = THIRD_NAME)
        val newStarWithId = Star(starId = 3, starName = THIRD_NAME)

        `when`(starRepo.save(newStar))
                .thenReturn(newStarWithId)

        val actualStar = starService.addStar(newStar);
        assertEquals(newStarWithId, actualStar)
    }

    @Test
    fun testUpdateStar() {
        val newStar = Star(starName = THIRD_NAME)
        val newStarWithId = Star(starId = 1, starName = THIRD_NAME)

        `when`(starRepo.save(newStarWithId))
                .thenReturn(newStarWithId)
        `when`(starRepo.findById(1))
                .thenReturn(Optional.of(expectedStars[0]))

        var actualStar = starService.updateStar(1, newStar)
        assertTrue(actualStar.isPresent)
        assertEquals(newStarWithId, actualStar.get())

        actualStar = starService.updateStar(3, newStar)
        assertFalse(actualStar.isPresent)
    }

    @Test
    fun testDeleteStar() {
        `when`(starRepo.findById(1))
                .thenReturn(Optional.of(expectedStars[0]))
                .thenReturn(Optional.empty())

        var actualStar = starService.deleteStar(1)
        assertTrue(actualStar.isPresent)
        assertEquals(expectedStars[0], actualStar.get())

        actualStar = starService.deleteStar(1)
        assertFalse(actualStar.isPresent)
    }

}