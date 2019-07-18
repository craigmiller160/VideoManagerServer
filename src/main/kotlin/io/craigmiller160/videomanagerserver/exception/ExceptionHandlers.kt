package io.craigmiller160.videomanagerserver.exception

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class ExceptionHandlers {

    @ExceptionHandler(BaseApiException::class)
    fun handleApiExceptions(res: HttpServletResponse, ex: BaseApiException) {
        res.sendError(ex.status.value(), ex.message)
    }

}