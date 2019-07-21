package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Role
import org.springframework.data.repository.CrudRepository

interface RoleRepository : CrudRepository<Role, Long>