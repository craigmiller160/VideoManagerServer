package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.userName = :userName AND u.password = :password")
    fun login(userName: String, password: String): User?

}