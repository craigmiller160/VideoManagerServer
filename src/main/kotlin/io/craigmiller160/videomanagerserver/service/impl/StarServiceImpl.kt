package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.repository.StarRepository
import io.craigmiller160.videomanagerserver.service.StarService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class StarServiceImpl @Autowired constructor(
        private val starRepo: StarRepository
): StarService {

    override fun getAllStars(): Set<Star> {
        return starRepo.findAll().toSet()
    }

    override fun getStar(starId: Long): Optional<Star> {
        return starRepo.findById(starId)
    }

    override fun addStar(star: Star): Star {
        return starRepo.save(star)
    }

    override fun updateStar(starId: Long, star: Star): Optional<Star> {
        star.starId = starId
        return starRepo.findById(starId)
                .map { starRepo.save(star) }
    }

    override fun deleteStar(starId: Long): Optional<Star> {
        val starOptional = starRepo.findById(starId)
        starRepo.deleteById(starId)
        return starOptional
    }
}