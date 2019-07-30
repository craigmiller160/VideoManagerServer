package io.craigmiller160.videomanagerserver.service.impl

import io.craigmiller160.videomanagerserver.dto.Star
import io.craigmiller160.videomanagerserver.repository.StarRepository
import io.craigmiller160.videomanagerserver.service.StarService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.Optional
import javax.transaction.Transactional

@Service
@Transactional
class StarServiceImpl @Autowired constructor(
        private val starRepo: StarRepository
): StarService {

    override fun getAllStars(): List<Star> {
        val sort = Sort.by(
                Sort.Order(Sort.Direction.ASC, "starName", Sort.NullHandling.NULLS_LAST)
        )
        return starRepo.findAll(sort).toList()
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