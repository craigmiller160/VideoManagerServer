package io.craigmiller160.videomanagerserver.exception

import org.springframework.http.HttpStatus

class ApiUnauthorizedException (
        message: String = "",
        cause: Throwable? = null
) : BaseApiException(message, cause, HttpStatus.UNAUTHORIZED)