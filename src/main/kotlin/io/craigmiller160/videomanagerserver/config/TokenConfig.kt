package io.craigmiller160.videomanagerserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.util.Base64
import javax.annotation.PostConstruct
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@Suppress("ConfigurationProperties")
@Configuration
@Validated
@ConfigurationProperties(prefix = "video.security.token")
data class TokenConfig (
        var expSecs: Int = 0,
        var refreshExpSecs: Int = 0,
        var keySizeBits: Int = 0,
        var videoExpSecs: Int = 0
) {

    lateinit var keyString: String
    lateinit var secretKey: SecretKey

    @PostConstruct
    fun createKey() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(keySizeBits)
        this.secretKey = keyGen.generateKey()
        this.keyString = Base64.getEncoder().encodeToString(secretKey.encoded)
    }
}
