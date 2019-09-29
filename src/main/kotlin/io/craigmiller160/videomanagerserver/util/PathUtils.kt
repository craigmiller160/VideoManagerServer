package io.craigmiller160.videomanagerserver.util

import java.net.URLDecoder

fun ensureTrailingSlash(value: String): String {
    if (value.endsWith("/")) return value
    return "$value/"
}

fun parseQueryString(queryString: String): Map<String,String> {
    val queryPairs = HashMap<String,String>()
    val pairs = queryString.split("&")
    pairs.filter { pair -> !pair.isBlank() }
            .forEach { pair ->
                val keyValue = pair.split("=")
                queryPairs += URLDecoder.decode(keyValue[0], "UTF-8") to keyValue[1]
            }
    return queryPairs
}
