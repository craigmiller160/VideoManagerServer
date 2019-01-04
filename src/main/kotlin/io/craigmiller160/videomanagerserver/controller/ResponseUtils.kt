package io.craigmiller160.videomanagerserver.controller

import org.springframework.http.ResponseEntity
import java.util.Optional

fun <T> okOrNoContent(result: Optional<T>): ResponseEntity<T> =
        result.map { ResponseEntity.ok(it) }
                .orElseGet { ResponseEntity.noContent().build() }