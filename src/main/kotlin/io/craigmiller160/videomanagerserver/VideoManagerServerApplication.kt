package io.craigmiller160.videomanagerserver

import io.craigmiller160.videomanagerserver.security.AllowAllHostnameVerifier
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.ClassPathResource
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


@SpringBootApplication
class VideoManagerServerApplication

private val logger = LoggerFactory.getLogger(VideoManagerServerApplication::class.java)

fun main(args: Array<String>) {
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    val trustStore = KeyStore.getInstance("JKS")
    val trustStoreStream = VideoManagerServerApplication::class.java.classLoader.getResourceAsStream("truststore.jks")
    logger.info("" + trustStoreStream) // TODO delete this
    trustStore.load(trustStoreStream, "changeit".toCharArray())
    trustManagerFactory.init(trustStore)
    val trustManagers = trustManagerFactory.trustManagers
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustManagers, null)
    SSLContext.setDefault(sslContext)

    HttpsURLConnection.setDefaultHostnameVerifier(AllowAllHostnameVerifier())

    Thread.setDefaultUncaughtExceptionHandler { _, e -> logger.error("Uncaught exception", e) }
    runApplication<VideoManagerServerApplication>(*args)
}

