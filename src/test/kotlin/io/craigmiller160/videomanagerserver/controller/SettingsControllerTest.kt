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
class SettingsControllerTest {

    @Test
    fun test_getSettings() {
        TODO("Finish this")
    }

    @Test
    fun test_getSettings_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_getSettings_missingRole() {
        TODO("Finish this")
    }

    @Test
    fun test_updateSettings() {
        TODO("Finish this")
    }

    @Test
    fun test_updateSettings_unauthorized() {
        TODO("Finish this")
    }

    @Test
    fun test_updateSettings_missingRole() {
        TODO("Finish this")
    }

}