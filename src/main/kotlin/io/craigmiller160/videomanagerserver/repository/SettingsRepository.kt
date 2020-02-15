package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.Settings
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SettingsRepository : CrudRepository<Settings,Long>
