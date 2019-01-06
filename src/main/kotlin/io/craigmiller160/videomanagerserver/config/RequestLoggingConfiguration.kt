package io.craigmiller160.videomanagerserver.config

import org.springframework.web.filter.CommonsRequestLoggingFilter

//@Configuration
class RequestLoggingConfiguration {

//    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        return loggingFilter
    }

}