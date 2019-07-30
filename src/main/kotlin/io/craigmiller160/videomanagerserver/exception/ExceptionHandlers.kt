package io.craigmiller160.videomanagerserver.exception

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
class ExceptionHandlers {

    @ExceptionHandler(ApiUnauthorizedException::class)
    fun handleApiExceptions(res: HttpServletResponse, ex: BaseApiException) {
        println("Working")
        res.sendError(ex.status.value(), ex.message)
    }

}