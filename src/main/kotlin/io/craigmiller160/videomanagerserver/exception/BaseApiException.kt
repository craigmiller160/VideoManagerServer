package io.craigmiller160.videomanagerserver.exception

import org.springframework.http.HttpStatus

open class BaseApiException (
        message: String = "",
        cause: Throwable? = null,
        val status: HttpStatus
) : Exception(message, cause)