package io.craigmiller160.videomanagerserver.service.file

import io.craigmiller160.videomanagerserver.dto.LocalFile
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.io.File
import java.nio.file.Files

@RunWith(MockitoJUnitRunner::class)
class LocalFileServiceTest {

    companion object {
        private const val TARGET_DIR_NAME = "targetDir"
        private const val HOME_DIR_NAME = "homeDir"
        private const val FILE_1_NAME = "file1"
        private const val FILE_2_NAME = "file2"
        private const val FILE_3_NAME = "file3"
        private const val FILE_4_NAME = "file4"
        private const val FILE_5_NAME = "file5"
        private const val DIR_1_NAME = "dir1"
        private const val DIR_2_NAME = "dir2"
        private const val TEXT = "Hello World"
    }

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var targetDir: File
    private lateinit var homeDir: File
    private lateinit var localFileService: LocalFileService

    @Before
    fun setup() {
        targetDir = tempFolder.newFolder(TARGET_DIR_NAME)
        Files.write(File(targetDir, FILE_1_NAME).toPath(), TEXT.toByteArray())
        Files.write(File(targetDir, FILE_2_NAME).toPath(), TEXT.toByteArray())
        Files.write(File(targetDir, FILE_3_NAME).toPath(), TEXT.toByteArray())
        File(targetDir, DIR_2_NAME).mkdirs()

        homeDir = tempFolder.newFolder(HOME_DIR_NAME)
        Files.write(File(homeDir, FILE_4_NAME).toPath(), TEXT.toByteArray())
        Files.write(File(homeDir, FILE_5_NAME).toPath(), TEXT.toByteArray())
        File(homeDir, DIR_1_NAME).mkdirs()

        localFileService = LocalFileService(homeDir.absolutePath)
    }

    @Test
    fun test_getFilesFromDirectory() {
        val files = localFileService.getFilesFromDirectory(targetDir.absolutePath, false)
        assertThat(files, allOf(
                hasProperty("rootPath", equalTo(targetDir.absolutePath)),
                hasProperty("files", allOf<List<LocalFile>>(
                        hasSize(4),
                        containsInAnyOrder<LocalFile>(
                                allOf(
                                        hasProperty("fileName", equalTo(FILE_1_NAME)),
                                        hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                                        hasProperty("directory", equalTo(false))
                                ),
                                allOf(
                                        hasProperty("fileName", equalTo(FILE_2_NAME)),
                                        hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                                        hasProperty("directory", equalTo(false))
                                ),
                                allOf(
                                        hasProperty("fileName", equalTo(FILE_3_NAME)),
                                        hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                                        hasProperty("directory", equalTo(false))
                                ),
                                allOf(
                                        hasProperty("fileName", equalTo(DIR_2_NAME)),
                                        hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                                        hasProperty("directory", equalTo(true))
                                )
                        )
                ))
        ))
    }

    @Test
    fun test_getFilesFromDirectory_onlyDirectories() {
        val files = localFileService.getFilesFromDirectory(targetDir.absolutePath, true)
        assertThat(files, allOf(
                hasProperty("rootPath", equalTo(targetDir.absolutePath)),
                hasProperty("files", allOf<List<LocalFile>>(
                        hasSize(1),
                        containsInAnyOrder<LocalFile>(
                                allOf(
                                        hasProperty("fileName", equalTo(DIR_2_NAME)),
                                        hasProperty("filePath", containsString(TARGET_DIR_NAME)),
                                        hasProperty("directory", equalTo(true))
                                )
                        )
                ))
        ))
    }

    @Test
    fun test_getFilesFromDirectory_noPath() {
        val files = localFileService.getFilesFromDirectory(null, false)
        assertThat(files, allOf(
                hasProperty("rootPath", equalTo(homeDir.absolutePath)),
                hasProperty("files", allOf<List<LocalFile>>(
                        hasSize(3),
                        containsInAnyOrder<LocalFile>(
                                allOf(
                                        hasProperty("fileName", equalTo(FILE_4_NAME)),
                                        hasProperty("filePath", containsString(HOME_DIR_NAME)),
                                        hasProperty("directory", equalTo(false))
                                ),
                                allOf(
                                        hasProperty("fileName", equalTo(FILE_5_NAME)),
                                        hasProperty("filePath", containsString(HOME_DIR_NAME)),
                                        hasProperty("directory", equalTo(false))
                                ),
                                allOf(
                                        hasProperty("fileName", equalTo(DIR_1_NAME)),
                                        hasProperty("filePath", containsString(HOME_DIR_NAME)),
                                        hasProperty("directory", equalTo(true))
                                )
                        )
                ))
        ))
    }

}
