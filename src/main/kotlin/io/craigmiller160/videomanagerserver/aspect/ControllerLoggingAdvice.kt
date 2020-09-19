/*
 *     video-manager-server
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.videomanagerserver.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import javax.servlet.http.HttpServletRequest

@Component
@Aspect
class ControllerLoggingAdvice {

    private val logger = LoggerFactory.getLogger(ControllerLoggingAdvice::class.java)

    private fun getRequest() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
    private fun getResponse() = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).response

    private fun buildPath(request: HttpServletRequest): String {
        return "${handleNull(request.contextPath)}${handleNull(request.servletPath)}${handleNull(request.pathInfo)}?${handleNull(request.queryString)}"
    }

    private fun handleNull(text: String?) = text ?: ""

    @Pointcut("execution(public * io.craigmiller160.videomanagerserver.controller.*Controller.*(..))")
    fun controllerPublicMethods() { }

    @Before("controllerPublicMethods()")
    fun logRequest(joinPoint: JoinPoint) {
        val request = getRequest()
        val path = buildPath(request)
        val method = request.method
        logger.debug("Request: $method $path = ${joinPoint.signature.name}()")
    }

    @AfterReturning("controllerPublicMethods()", returning = "result")
    fun logResponseAfterReturning(joinPoint: JoinPoint, result: Any?) {
        val request = getRequest()
        val responseEntity = if(result is ResponseEntity<*>) result else null
        val path = buildPath(request)
        val method = request.method
        val status = responseEntity?.statusCode?.value() ?: 0
        logger.debug("Response $status: $method $path = ${joinPoint.signature.name}()")
    }

    @AfterThrowing("controllerPublicMethods()", throwing = "throwing")
    fun logResponseAfterThrowing(joinPoint: JoinPoint, throwing: Throwable) {
        val request = getRequest()
        val path = buildPath(request)
        val method = request.method
        logger.error("Response Error: $method $path = ${joinPoint.signature.name}()", throwing)
    }

}
