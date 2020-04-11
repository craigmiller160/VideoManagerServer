package io.craigmiller160.videomanagerserver

import io.craigmiller160.videomanagerserver.security.AllowAllHostnameVerifier
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import javax.net.ssl.HttpsURLConnection

@SpringBootApplication
class VideoManagerServerApplication

private val logger = LoggerFactory.getLogger(VideoManagerServerApplication::class.java)

fun main(args: Array<String>) {
    val trustStorePath = ClassLoader.getSystemClassLoader().getResource("truststore.jks")?.toURI()?.path
    System.setProperty("javax.net.ssl.trustStore", trustStorePath!!)
    System.setProperty("javax.net.ssl.trustStorePassword", "password")
    HttpsURLConnection.setDefaultHostnameVerifier(AllowAllHostnameVerifier())

    Thread.setDefaultUncaughtExceptionHandler { _, e -> logger.error("Uncaught exception", e) }
    runApplication<VideoManagerServerApplication>(*args)
}

