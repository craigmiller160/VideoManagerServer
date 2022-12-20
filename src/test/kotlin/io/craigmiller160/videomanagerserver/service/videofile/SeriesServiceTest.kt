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

import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.config.MapperConfig
import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import java.util.Optional
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort

@RunWith(MockitoJUnitRunner::class)
class SeriesServiceTest {

  companion object {

    private const val FIRST_NAME = "FirstName"
    private const val SECOND_NAME = "SecondName"
    private const val THIRD_NAME = "ThirdName"

    private val expectedSeries =
      listOf(
        Series(seriesId = 1, seriesName = FIRST_NAME),
        Series(seriesId = 2, seriesName = SECOND_NAME))

    private val expectedSeriesPayloads =
      listOf(
        SeriesPayload(seriesId = 1, seriesName = FIRST_NAME),
        SeriesPayload(seriesId = 2, seriesName = SECOND_NAME))
  }

  @Mock private lateinit var seriesRepo: SeriesRepository
  @Mock private lateinit var fileSeriesRepo: FileSeriesRepository
  @InjectMocks private lateinit var seriesService: SeriesService
  @Spy private var modelMapper = MapperConfig().modelMapper()

  @Test
  fun testGetAllSeries() {
    Mockito.`when`(seriesRepo.findAll(ArgumentMatchers.isA(Sort::class.java)))
      .thenReturn(expectedSeries)

    val actualSeries = seriesService.getAllSeries()
    Assert.assertNotNull(actualSeries)
    assertEquals(expectedSeriesPayloads, actualSeries)
  }

  @Test
  fun testGetSeries() {
    Mockito.`when`(seriesRepo.findById(1)).thenReturn(Optional.of(expectedSeries[0]))
    Mockito.`when`(seriesRepo.findById(2)).thenReturn(Optional.of(expectedSeries[1]))

    var actualSeries = seriesService.getSeries(1)
    assertNotNull(actualSeries)
    assertEquals(expectedSeriesPayloads[0], actualSeries)

    actualSeries = seriesService.getSeries(2)
    assertNotNull(actualSeries)
    assertEquals(expectedSeriesPayloads[1], actualSeries)

    actualSeries = seriesService.getSeries(3)
    assertNull(actualSeries)
  }

  @Test
  fun testAddSeries() {
    val newSeries = Series(seriesName = THIRD_NAME)
    val newSeriesWithId = Series(seriesId = 3, seriesName = THIRD_NAME)
    val newSeriesPayload = SeriesPayload(seriesName = THIRD_NAME)
    val newSeriesWithIdPayload = SeriesPayload(seriesId = 3, seriesName = THIRD_NAME)

    Mockito.`when`(seriesRepo.save(newSeries)).thenReturn(newSeriesWithId)

    val actualSeries = seriesService.addSeries(newSeriesPayload)
    Assert.assertEquals(newSeriesWithIdPayload, actualSeries)
  }

  @Test
  fun testUpdateSeries() {
    val newSeries = Series(seriesName = THIRD_NAME)
    val newSeriesWithId = Series(seriesId = 3, seriesName = THIRD_NAME)
    val newSeriesPayload = SeriesPayload(seriesName = THIRD_NAME)
    val newSeriesWithIdPayload = SeriesPayload(seriesId = 3, seriesName = THIRD_NAME)

    Mockito.`when`(seriesRepo.save(newSeriesWithId)).thenReturn(newSeriesWithId)
    Mockito.`when`(seriesRepo.findById(3)).thenReturn(Optional.of(expectedSeries[0]))

    var actualSeries = seriesService.updateSeries(3, newSeriesPayload)
    assertNotNull(actualSeries)
    Assert.assertEquals(newSeriesWithIdPayload, actualSeries)

    actualSeries = seriesService.updateSeries(1, newSeriesPayload)
    assertNull(actualSeries)
  }

  @Test
  fun test_deleteSeries() {
    Mockito.`when`(seriesRepo.findById(1))
      .thenReturn(Optional.of(expectedSeries[0]))
      .thenReturn(Optional.empty())

    var actualSeries = seriesService.deleteSeries(1)
    assertNotNull(actualSeries)
    assertEquals(expectedSeriesPayloads[0], actualSeries)

    actualSeries = seriesService.deleteSeries(1)
    assertNull(actualSeries)

    verify(seriesRepo, Mockito.times(2)).deleteById(1)
    verify(fileSeriesRepo, Mockito.times(2)).deleteAllBySeriesId(1)
  }
}
