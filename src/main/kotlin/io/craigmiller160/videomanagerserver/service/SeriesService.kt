package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.Series

interface SeriesService {

    fun addSeries(series: Series)

    fun updateSeries(seriesId: Long, series: Series)

    fun deleteSeries(seriesId: Long)

}