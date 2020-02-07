package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.entity.Series
import java.util.Optional

interface SeriesService {

    fun getAllSeries(): List<Series>

    fun getSeries(seriesId: Long): Optional<Series>

    fun addSeries(series: Series): Series

    fun updateSeries(seriesId: Long, series: Series): Optional<Series>

    fun deleteSeries(seriesId: Long): Optional<Series>

}