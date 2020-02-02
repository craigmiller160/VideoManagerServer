package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.AppUser
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository : CrudRepository<AppUser, Long> {

    fun findByUserName(userName: String): AppUser?

}
