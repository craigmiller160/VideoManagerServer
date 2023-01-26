package io.craigmiller160.videomanagerserver.test_util

import io.craigmiller160.testcontainers.common.TestcontainersExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(TestcontainersExtension::class, SpringExtension::class)
@SpringBootTest
annotation class VideoManagerIntegrationTest()
