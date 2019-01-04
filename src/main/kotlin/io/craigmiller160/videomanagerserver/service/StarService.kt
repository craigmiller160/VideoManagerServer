package io.craigmiller160.videomanagerserver.service

import io.craigmiller160.videomanagerserver.dto.Star
import java.util.Optional

interface StarService {

    fun getAllStars(): List<Star>

    fun getStar(starId: Long): Optional<Star>

    fun addStar(star: Star): Star

    fun updateStar(starId: Long, star: Star): Optional<Star>

    fun deleteStar(starId: Long): Optional<Star>

}