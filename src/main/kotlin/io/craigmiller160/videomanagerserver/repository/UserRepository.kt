package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {

    fun findByUserName(userName: String): User?

}