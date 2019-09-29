package io.craigmiller160.videomanagerserver.config

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class TokenConfigIntegrationTest {

    @Autowired
    private lateinit var tokenConfig: TokenConfig

    @Test
    fun test_expSecs() {
        TODO("Finish this")
    }

}
