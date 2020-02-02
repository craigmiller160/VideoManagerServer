package io.craigmiller160.videomanagerserver.repository

import io.craigmiller160.videomanagerserver.entity.AppUser
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
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
class AppUserRepositoryIntegrationTest {

    companion object {
        private const val USER_NAME = "userName"
        private const val PASSWORD = "password"
    }

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Before
    fun setup() {
        val user = AppUser(userName = USER_NAME, password = PASSWORD)
        appUserRepository.save(user)
    }

    @After
    fun clean() {
        appUserRepository.deleteAll()
    }

    @Test
    fun test_findByUserName() {
        val user = appUserRepository.findByUserName(USER_NAME)
        assertThat(user, allOf(
                hasProperty("userName", equalTo(USER_NAME)),
                hasProperty("password", equalTo(PASSWORD))
        ))
    }

    @Test
    fun test_findByUserName_noUser() {
        val user = appUserRepository.findByUserName("Bob")
        assertNull(user)
    }

}
