package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Series
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import io.craigmiller160.videomanagerserver.service.SeriesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class SeriesServiceImpl @Autowired constructor(
        private val seriesRepo: SeriesRepository
) : SeriesService {

    override fun getAllSeries(): Set<Series> {
        return seriesRepo.findAll().toSet()
    }

    override fun getSeries(seriesId: Long): Optional<Series> {
        return seriesRepo.findById(seriesId)
    }

    override fun addSeries(series: Series): Series {
        return seriesRepo.save(series)
    }

    override fun updateSeries(seriesId: Long, series: Series): Optional<Series> {
        series.seriesId = seriesId
        return seriesRepo.findById(seriesId)
                .map { seriesRepo.save(series) }
    }

    override fun deleteSeries(seriesId: Long): Optional<Series> {
        val seriesOptional = seriesRepo.findById(seriesId)
        seriesRepo.deleteById(seriesId)
        return seriesOptional
    }
}