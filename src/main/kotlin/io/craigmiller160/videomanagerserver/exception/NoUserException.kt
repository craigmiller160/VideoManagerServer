package io.craigmiller160.videomanagerserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class NoUserException (
        message: String = "",
        cause: Throwable? = null
) : BaseApiException(message, cause, HttpStatus.BAD_REQUEST)