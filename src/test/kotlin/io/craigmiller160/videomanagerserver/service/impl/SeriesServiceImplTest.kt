package io.craigmiller160.videomanagerserver.service.impl

import com.nhaarman.mockito_kotlin.verify
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.isA
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort
import java.util.Optional

@RunWith(MockitoJUnitRunner::class)
class SeriesServiceImplTest {





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
    fun test_deleteSeries() {
        `when`(seriesRepo.findById(1))
                .thenReturn(Optional.of(expectedSeries[0]))
                .thenReturn(Optional.empty())

        var actualSeries = seriesService.deleteSeries(1)
        assertTrue(actualSeries.isPresent)
        assertEquals(expectedSeries[0], actualSeries.get())

        actualSeries = seriesService.deleteSeries(1)
        assertFalse(actualSeries.isPresent)

        verify(seriesRepo, times(2))
                .deleteById(1)
        verify(fileSeriesRepo, times(2))
                .deleteAllBySeriesId(1)
    }

}