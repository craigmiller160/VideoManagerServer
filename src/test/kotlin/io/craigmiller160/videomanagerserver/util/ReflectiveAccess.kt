package io.craigmiller160.videomanagerserver.util

import org.springframework.util.ReflectionUtils

fun <T> getField(obj: Any, fieldName: String, clazz: Class<T>): T {
    val field = ReflectionUtils.findField(obj.javaClass, fieldName)
    return field?.let { f ->
        ReflectionUtils.makeAccessible(f)
        clazz.cast(f.get(obj))
    } ?: throw ReflectiveOperationException("Unable to access the field $fieldName")
}