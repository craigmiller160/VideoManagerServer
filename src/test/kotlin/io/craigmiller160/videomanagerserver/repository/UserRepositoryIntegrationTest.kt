package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.dto.User
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest {

    companion object {
        private const val USER_NAME = "userName"
        private const val PASSWORD = "password"
    }

    @Autowired
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        val user = User(userName = USER_NAME, password = PASSWORD)
        userRepository.save(user)
    }

    @After
    fun clean() {
        userRepository.deleteAll()
    }

    @Test
    fun test_login() {
        val user = userRepository.login(USER_NAME, PASSWORD)
        assertNotNull(user)
    }

}