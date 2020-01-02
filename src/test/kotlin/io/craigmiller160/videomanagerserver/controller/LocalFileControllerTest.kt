package io.craigmiller160.videomanagerserver.controller

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest
@WebAppConfiguration
@ContextConfiguration
class LocalFileControllerTest {

    @Test
    fun test_getFilesFromDirectory() {
        TODO("Finish this")
    }

    @Test
    fun test_getFilesFromDirectory_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_getFilesFromDirectory_missingRole() {
        TODO("Finish this")
    }

}
