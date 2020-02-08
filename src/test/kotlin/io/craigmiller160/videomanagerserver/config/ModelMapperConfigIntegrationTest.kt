package io.craigmiller160.videomanagerserver.config

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class ModelMapperConfigIntegrationTest {

    @Autowired
    private lateinit var modelMapperConfig: ModelMapperConfig

    @Test
    fun test_convertVideoFileToVideoFilePayload() {
        TODO("Finish this")
    }

    @Test
    fun test_convertVideoFilePayloadToVideoFile() {
        TODO("Finish this")
    }

}
