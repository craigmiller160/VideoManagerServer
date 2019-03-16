package io.craigmiller160.videomanagerserver.test_util

fun <T> getFirst(set: Set<T>) = set.stream().findFirst().get()

fun <T> getIndex(set: Set<T>, index: Long) = set.stream().skip(index).findFirst().get()