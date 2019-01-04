package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Optional

class SeriesServiceImplTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"

        private val expectedSeries = listOf(
                Series(seriesId = 1, seriesName = FIRST_NAME),
                Series(seriesId = 2, seriesName = SECOND_NAME)
        )

    }

    private lateinit var seriesService: SeriesServiceImpl

    @Mock
    private lateinit var seriesRepo: SeriesRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        seriesService = SeriesServiceImpl(seriesRepo)
    }

    @Test
    fun testGetAllSeries() {
        `when`(seriesRepo.findAll())
                .thenReturn(expectedSeries)

        val actualSeries = seriesService.getAllSeries()
        assertNotNull(actualSeries)
        assertEquals(expectedSeries.toSet(), actualSeries)
    }

    @Test
    fun testGetSeries() {
        `when`(seriesRepo.findById(1))
                .thenReturn(Optional.of(expectedSeries[0]))
        `when`(seriesRepo.findById(2))
                .thenReturn(Optional.of(expectedSeries[1]))

        var actualSeries = seriesService.getSeries(1)
        assertTrue(actualSeries.isPresent)
        assertEquals(expectedSeries[0], actualSeries.get())

        actualSeries = seriesService.getSeries(2)
        assertTrue(actualSeries.isPresent)
        assertEquals(expectedSeries[1], actualSeries.get())

        actualSeries = seriesService.getSeries(3)
        assertFalse(actualSeries.isPresent)
    }

    @Test
    fun testAddSeries() {
        val newSeries = Series(seriesName = THIRD_NAME)
        val newSeriesWithId = Series(seriesId = 3, seriesName = THIRD_NAME)

        `when`(seriesRepo.save(newSeries))
                .thenReturn(newSeriesWithId)

        val actualSeries = seriesService.addSeries(newSeries)
        assertEquals(newSeriesWithId, actualSeries)
    }

    @Test
    fun testUpdateSeries() {
        val newSeries = Series(seriesName = THIRD_NAME)
        val newSeriesWithId = Series(seriesId = 3, seriesName = THIRD_NAME)

        `when`(seriesRepo.save(newSeries))
                .thenReturn(newSeriesWithId)
        `when`(seriesRepo.findById(1))
                .thenReturn(Optional.of(expectedSeries[0]))

        var actualSeries = seriesService.updateSeries(1, newSeries)
        assertTrue(actualSeries.isPresent)
        assertEquals(newSeriesWithId, actualSeries.get())

        actualSeries = seriesService.updateSeries(3, newSeries)
        assertFalse(actualSeries.isPresent)
    }

    @Test
    fun testDeleteSeries() {
        `when`(seriesRepo.findById(1))
                .thenReturn(Optional.of(expectedSeries[0]))
                .thenReturn(Optional.empty())

        var actualSeries = seriesService.deleteSeries(1)
        assertTrue(actualSeries.isPresent)
        assertEquals(expectedSeries[0], actualSeries.get())

        actualSeries = seriesService.deleteSeries(1)
        assertFalse(actualSeries.isPresent)
    }

}