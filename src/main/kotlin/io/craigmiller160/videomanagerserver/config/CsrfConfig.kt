package io.craigmiller160.videomanagerserver.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = [
    "io.craigmiller160.csrf.spring"
])
class CsrfConfig