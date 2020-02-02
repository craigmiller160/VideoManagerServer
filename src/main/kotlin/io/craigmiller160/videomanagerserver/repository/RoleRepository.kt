package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.Role
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : CrudRepository<Role, Long>
