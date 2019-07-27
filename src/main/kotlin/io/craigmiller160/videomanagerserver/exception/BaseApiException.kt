package io.craigmiller160.videomanagerserver.exception

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

open class BaseApiException (
        message: String = "",
        cause: Throwable? = null,
        val status: HttpStatus
) : RuntimeException(message, cause)