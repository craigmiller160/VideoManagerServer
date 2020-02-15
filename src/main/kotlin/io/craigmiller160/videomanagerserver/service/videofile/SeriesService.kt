package io.craigmiller160.videomanagerserver.service.videofile

import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class SeriesService (
        private val seriesRepo: SeriesRepository,
        private val fileSeriesRepo: FileSeriesRepository
) {

    private val modelMapper = ModelMapper()

    fun getAllSeries(): List<SeriesPayload> {
        val sort = Sort.by(
                Sort.Order(Sort.Direction.ASC, "seriesName", Sort.NullHandling.NULLS_LAST)
        )
        return seriesRepo.findAll(sort)
                .map { series -> modelMapper.map(series, SeriesPayload::class.java) }
    }

    fun getSeries(seriesId: Long): SeriesPayload? {
        return seriesRepo.findById(seriesId)
                .map { series -> modelMapper.map(series, SeriesPayload::class.java) }
                .orElse(null)
    }

    fun addSeries(payload: SeriesPayload): SeriesPayload {
        val series = modelMapper.map(payload, Series::class.java)
        val savedSeries = seriesRepo.save(series)
        return modelMapper.map(savedSeries, SeriesPayload::class.java)
    }

    fun updateSeries(seriesId: Long, payload: SeriesPayload): SeriesPayload? {
        return seriesRepo.findById(seriesId)
                .map { _ ->
                    val series = modelMapper.map(payload, Series::class.java)
                    series.seriesId = seriesId
                    val updatedSeries = seriesRepo.save(series)
                    modelMapper.map(updatedSeries, SeriesPayload::class.java)
                }
                .orElse(null)
    }

    fun deleteSeries(seriesId: Long): SeriesPayload? {
        val seriesOptional = seriesRepo.findById(seriesId)
        fileSeriesRepo.deleteAllBySeriesId(seriesId)
        seriesRepo.deleteById(seriesId)
        return seriesOptional
                .map { series -> modelMapper.map(series, SeriesPayload::class.java) }
                .orElse(null)
    }

}