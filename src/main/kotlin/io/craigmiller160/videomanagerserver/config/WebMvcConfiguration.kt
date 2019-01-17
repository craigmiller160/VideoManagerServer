package io.craigmiller160.videomanagerserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class WebMvcConfiguration {

    @Bean
    fun corsFilter(@Value("\${cors.origins}") origins: String): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        if (origins.isNotEmpty()) {
            config.allowedOrigins = origins.split(",").toList()
            config.allowedMethods = listOf("GET", "POST", "PUT", "OPTIONS", "DELETE")
            config.allowedHeaders = listOf("Origin", "Content-Type", "Accept")
        }
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

}