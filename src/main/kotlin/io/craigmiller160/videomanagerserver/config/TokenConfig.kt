package io.craigmiller160.videomanagerserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.util.Base64
import javax.annotation.PostConstruct
import javax.crypto.KeyGenerator

@Suppress("ConfigurationProperties")
@Configuration
@Validated
@ConfigurationProperties(prefix = "video.security.token")
data class TokenConfig (
        var expSecs: Int = 0,
        var refreshExpSecs: Int = 0,
        var keySizeBits: Int = 0,
        @Value("\${video.expSecs}")
        var videoExpSecs: Int = 0
) {

    lateinit var key: String

    @PostConstruct
    fun createKey() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(keySizeBits)
        val secretKey = keyGen.generateKey()
        this.key = Base64.getEncoder().encodeToString(secretKey.encoded)
    }
}