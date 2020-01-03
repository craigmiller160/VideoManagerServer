package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Settings
import org.springframework.data.repository.CrudRepository

interface SettingsRepository : CrudRepository<Settings,Long>