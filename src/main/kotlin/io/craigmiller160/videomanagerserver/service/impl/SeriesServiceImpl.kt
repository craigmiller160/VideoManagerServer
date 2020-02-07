package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import io.craigmiller160.videomanagerserver.service.SeriesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.Optional
import javax.transaction.Transactional

@Service
@Transactional
class SeriesServiceImpl @Autowired constructor(
        private val seriesRepo: SeriesRepository,
        private val fileSeriesRepo: FileSeriesRepository
) : SeriesService {

    override fun getAllSeries(): List<Series> {
        val sort = Sort.by(
                Sort.Order(Sort.Direction.ASC, "seriesName", Sort.NullHandling.NULLS_LAST)
        )
        return seriesRepo.findAll(sort).toList()
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
        fileSeriesRepo.deleteAllBySeriesId(seriesId)
        seriesRepo.deleteById(seriesId)
        return seriesOptional
    }
}
