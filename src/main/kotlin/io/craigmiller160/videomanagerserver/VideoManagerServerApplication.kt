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

private const val TRUST_STORE_TYPE = "JKS"
private const val TRUST_STORE_PATH = "truststore.jks"
private const val TRUST_STORE_PASSWORD = "changeit"

private val logger = LoggerFactory.getLogger(VideoManagerServerApplication::class.java)

fun main(args: Array<String>) {
    setupTls()
    Thread.setDefaultUncaughtExceptionHandler { _, e -> logger.error("Uncaught exception", e) }
    runApplication<VideoManagerServerApplication>(*args)
}

fun setupTls() {
    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

    val trustStore = KeyStore.getInstance(TRUST_STORE_TYPE)
    val trustStoreStream = VideoManagerServerApplication::class.java.classLoader.getResourceAsStream(TRUST_STORE_PATH)
    trustStore.load(trustStoreStream, TRUST_STORE_PASSWORD.toCharArray())

    trustManagerFactory.init(trustStore)

    val trustManagers = trustManagerFactory.trustManagers
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(null, trustManagers, null)
    SSLContext.setDefault(sslContext)

    HttpsURLConnection.setDefaultHostnameVerifier(AllowAllHostnameVerifier())
}