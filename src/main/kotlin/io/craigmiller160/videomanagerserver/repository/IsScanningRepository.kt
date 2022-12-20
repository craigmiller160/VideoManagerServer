package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.IsScanning
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IsScanningRepository : JpaRepository<IsScanning, Long>