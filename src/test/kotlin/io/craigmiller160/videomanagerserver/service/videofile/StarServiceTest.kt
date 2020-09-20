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

package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.config.MapperConfig
import io.craigmiller160.videomanagerserver.dto.StarPayload
import io.craigmiller160.videomanagerserver.entity.Star
import io.craigmiller160.videomanagerserver.repository.FileStarRepository
import io.craigmiller160.videomanagerserver.repository.StarRepository
import org.junit.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort
import java.util.Optional
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(MockitoJUnitRunner::class)
class StarServiceTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"

        private val expectedStars = listOf(
                Star(starId = 1, starName = FIRST_NAME),
                Star(starId = 2, starName = SECOND_NAME)
        )

        private val expectedStarPayloads = listOf(
                StarPayload(starId = 1, starName = FIRST_NAME),
                StarPayload(starId = 2, starName = SECOND_NAME)
        )

    }

    @Mock
    private lateinit var starRepo: StarRepository
    @Mock
    private lateinit var fileStarRepo: FileStarRepository
    @InjectMocks
    private lateinit var starService: StarService
    @Spy
    private var modelMapper = MapperConfig().modelMapper()

    @Test
    fun testGetAllStars() {
        Mockito.`when`(starRepo.findAll(ArgumentMatchers.isA(Sort::class.java)))
                .thenReturn(expectedStars)

        val actualStars = starService.getAllStars()
        Assert.assertNotNull(actualStars)
        assertEquals(expectedStarPayloads.size, actualStars.size)
        assertEquals(expectedStarPayloads, actualStars)
    }

    @Test
    fun testGetStar() {
        Mockito.`when`(starRepo.findById(1))
                .thenReturn(Optional.of(expectedStars[0]))
        Mockito.`when`(starRepo.findById(2))
                .thenReturn(Optional.of(expectedStars[1]))

        var actualStar = starService.getStar(1)
        assertNotNull(actualStar)
        assertEquals(expectedStarPayloads[0], actualStar)

        actualStar = starService.getStar(2)
        assertNotNull(actualStar)
        assertEquals(expectedStarPayloads[1], actualStar)

        actualStar = starService.getStar(3)
        assertNull(actualStar)
    }

    @Test
    fun testAddStar() {
        val newStar = Star(starName = THIRD_NAME)
        val newStarWithId = Star(starId = 3, starName = THIRD_NAME)
        val newStarPayload = StarPayload(starName = THIRD_NAME)
        val newStarPayloadWithId = StarPayload(starId = 3, starName = THIRD_NAME)

        Mockito.`when`(starRepo.save(newStar))
                .thenReturn(newStarWithId)

        val actualStar = starService.addStar(newStarPayload);
        Assert.assertEquals(newStarPayloadWithId, actualStar)
    }

    @Test
    fun testUpdateStar() {
        val newStar = Star(starName = THIRD_NAME)
        val newStarWithId = Star(starId = 1, starName = THIRD_NAME)
        val newStarPayload = StarPayload(starName = THIRD_NAME)
        val newStarPayloadWithId = StarPayload(starId = 1, starName = THIRD_NAME)

        Mockito.`when`(starRepo.save(newStarWithId))
                .thenReturn(newStarWithId)
        Mockito.`when`(starRepo.findById(1))
                .thenReturn(Optional.of(expectedStars[0]))

        var actualStar = starService.updateStar(1, newStarPayload)
        assertNotNull(actualStar)
        Assert.assertEquals(newStarPayloadWithId, actualStar)

        actualStar = starService.updateStar(3, newStarPayload)
        assertNull(actualStar)
    }

    @Test
    fun test_deleteStar() {
        Mockito.`when`(starRepo.findById(1))
                .thenReturn(Optional.of(expectedStars[0]))
                .thenReturn(Optional.empty())

        var actualStar = starService.deleteStar(1)
        assertNotNull(actualStar)
        assertEquals(expectedStarPayloads[0], actualStar)

        actualStar = starService.deleteStar(1)
        assertNull(actualStar)

        Mockito.verify(starRepo, Mockito.times(2))
                .deleteById(1)
        Mockito.verify(fileStarRepo, Mockito.times(2))
                .deleteAllByStarId(1)
    }

}
