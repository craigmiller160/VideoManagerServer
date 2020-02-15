package io.craigmiller160.videomanagerserver.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.json.JacksonTester
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

abstract class AbstractControllerTest {

    @Autowired
    private lateinit var webAppContext: WebApplicationContext

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    protected lateinit var mockMvcHandler: MockMvcHandler

    @Before
    open fun setup() {
        mockMvcHandler = buildMockMvcHandler()
        JacksonTester.initFields(this, objectMapper)
    }

    protected fun buildMockMvcHandler(): MockMvcHandler {
        val mockMvc = MockMvcBuilders
                .webAppContextSetup(webAppContext)
                .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
                .build()
        return MockMvcHandler(mockMvc)
    }


}
