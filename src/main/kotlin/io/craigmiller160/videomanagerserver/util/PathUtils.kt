package io.craigmiller160.videomanagerserver.util

import java.net.URLDecoder

fun ensureTrailingSlash(value: String): String {
    if (value.endsWith("/")) return value
    return "$value/"
}

// TODO create unit test
fun parseQueryString(queryString: String): Map<String,String> {
    val queryPairs = HashMap<String,String>()
    val pairs = queryString.split("&")
    pairs.forEach { pair ->
        val keyValue = pair.split("=")
        queryPairs += URLDecoder.decode(keyValue[0], "UTF-8") to URLDecoder.decode(keyValue[1], "UTF-8")
    }
    return queryPairs
}