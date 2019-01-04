package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.Star

interface StarService {

    fun addStar(star: Star)

    fun updateStar(starId: Long, star: Star)

    fun deleteStar(starId: Long)

}