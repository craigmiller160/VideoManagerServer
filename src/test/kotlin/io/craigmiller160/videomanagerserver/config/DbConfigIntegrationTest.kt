package io.craigmiller160.videomanagerserver.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class DbConfigIntegrationTest {

    companion object {
        private const val URL = "jdbc:h2:mem:testdb"
        private const val USERNAME = "user"
        private const val PASSWORD = "password"
        private const val DRIVER_CLASS_NAME = "org.h2.Driver"
        private const val DB = "db_test"
    }

    @Autowired
    private lateinit var dbConfig: DbConfig

    @Test
    fun testUrl() {
        assertEquals(URL, dbConfig.url)
    }

    @Test
    fun testUsername() {
        assertEquals(USERNAME, dbConfig.username)
    }

    @Test
    fun testPassword() {
        assertEquals(PASSWORD, dbConfig.password)
    }

    @Test
    fun testDriverClassName() {
        assertEquals(DRIVER_CLASS_NAME, dbConfig.driverClassName)
    }

    @Test
    fun testDb() {
        assertEquals(DB, dbConfig.db)
    }

    @Test
    fun testDocker() {
        assertTrue(dbConfig.docker)
    }

}