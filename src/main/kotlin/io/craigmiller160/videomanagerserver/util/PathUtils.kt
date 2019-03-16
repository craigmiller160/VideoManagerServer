package io.craigmiller160.videomanagerserver.util

fun ensureTrailingSlash(value: String): String {
    if (value.endsWith("/")) return value
    return "$value/"
}