package io.craigmiller160.videomanagerserver.util

import org.mockito.Mockito

fun <T> isA(clazz: Class<T>): T = Mockito.isA(clazz)