package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.domain.Sort

@RunWith(MockitoJUnitRunner::class)
class SeriesServiceTest {

    companion object {

        private const val FIRST_NAME = "FirstName"
        private const val SECOND_NAME = "SecondName"
        private const val THIRD_NAME = "ThirdName"

        private val expectedSeries = listOf(
                Series(seriesId = 1, seriesName = FIRST_NAME),
                Series(seriesId = 2, seriesName = SECOND_NAME)
        )

        private val expectedSeriesPayloads = listOf(
                SeriesPayload(seriesId = 1, seriesName = FIRST_NAME),
                SeriesPayload(seriesId = 1, seriesName = SECOND_NAME)
        )

    }

    @Mock
    private lateinit var seriesRepo: SeriesRepository
    @Mock
    private lateinit var fileSeriesRepo: FileSeriesRepository
    @InjectMocks
    private lateinit var seriesService: SeriesService

    @Test
    fun testGetAllSeries() {
        Mockito.`when`(seriesRepo.findAll(ArgumentMatchers.isA(Sort::class.java)))
                .thenReturn(expectedSeries)

        val actualSeries = seriesService.getAllSeries()
        Assert.assertNotNull(actualSeries)
        assertEquals(expectedSeries, actualSeries)
    }

}