package io.craigmiller160.videomanagerserver.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Aspect
class ServiceLoggingAdvice {

    private val logger = LoggerFactory.getLogger(ServiceLoggingAdvice::class.java)

    @Pointcut("execution(public * io.craigmiller160.videomanagerserver.service.impl.*ServiceImpl.*(..))")
    fun servicePublicMethods() { }

    @Before("servicePublicMethods()")
    fun logMethodCall(joinPoint: JoinPoint) {
        logger.trace("Entering: ${joinPoint.signature.name}")
    }

}