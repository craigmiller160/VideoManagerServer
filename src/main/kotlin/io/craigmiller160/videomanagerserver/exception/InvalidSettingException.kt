package io.craigmiller160.videomanagerserver.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InvalidSettingException (
        message: String = "",
        cause: Throwable? = null
) : BaseApiException(message, cause, HttpStatus.INTERNAL_SERVER_ERROR)