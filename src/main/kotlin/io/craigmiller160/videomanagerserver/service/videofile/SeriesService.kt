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

import io.craigmiller160.videomanagerserver.dto.SeriesPayload
import io.craigmiller160.videomanagerserver.entity.Series
import io.craigmiller160.videomanagerserver.mapper.VMModelMapper
import io.craigmiller160.videomanagerserver.repository.FileSeriesRepository
import io.craigmiller160.videomanagerserver.repository.SeriesRepository
import javax.transaction.Transactional
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
@Transactional
class SeriesService(
  private val seriesRepo: SeriesRepository,
  private val fileSeriesRepo: FileSeriesRepository,
  private val modelMapper: VMModelMapper
) {

  fun getAllSeries(): List<SeriesPayload> {
    val sort = Sort.by(Sort.Order(Sort.Direction.ASC, "seriesName", Sort.NullHandling.NULLS_LAST))
    return seriesRepo.findAll(sort).map { series ->
      modelMapper.map(series, SeriesPayload::class.java)
    }
  }

  fun getSeries(seriesId: Long): SeriesPayload? {
    return seriesRepo
      .findById(seriesId)
      .map { series -> modelMapper.map(series, SeriesPayload::class.java) }
      .orElse(null)
  }

  fun addSeries(payload: SeriesPayload): SeriesPayload {
    val series = modelMapper.map(payload, Series::class.java)
    val savedSeries = seriesRepo.save(series)
    return modelMapper.map(savedSeries, SeriesPayload::class.java)
  }

  fun updateSeries(seriesId: Long, payload: SeriesPayload): SeriesPayload? {
    return seriesRepo
      .findById(seriesId)
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
