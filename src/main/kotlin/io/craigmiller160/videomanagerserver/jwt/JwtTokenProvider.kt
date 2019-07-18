package io.craigmiller160.videomanagerserver.jwt

import io.craigmiller160.videomanagerserver.config.TokenConfig
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class JwtTokenProvider (
       private val tokenConfig: TokenConfig
) {

    private var secretKey: String = ""

    @PostConstruct
    protected fun init() {
        secretKey = Base64.getEncoder().encodeToString(tokenConfig.key.toByteArray())
    }


    // TODO finish implementing the parts that I need here

}