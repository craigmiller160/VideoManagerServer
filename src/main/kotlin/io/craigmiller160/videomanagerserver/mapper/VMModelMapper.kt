package io.craigmiller160.videomanagerserver.mapper

import org.modelmapper.ModelMapper

class VMModelMapper {

    private val mapper = ModelMapper()

    fun <T> map(source: Any, destType: Class<T>): T {
        return mapper.map(source, destType)
    }

    fun <T> mapWithExisting(source: Any, destType: Class<T>, existing: T): T {
        TODO("Figure out better name and implementation")
    }

}