package io.craigmiller160.videomanagerserver.test_util

import io.craigmiller160.testcontainers.common.core.AuthenticationHelper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthenticationConfig {
  companion object {
    private const val EDIT = "EDIT"
    private const val SCAN = "SCAN"
    private const val ADMIN = "ADMIN"
  }
  @Bean fun authenticationHelper() = AuthenticationHelper()

  @Bean
  fun defaultUsers(authHelper: AuthenticationHelper): DefaultUsers {
    authHelper.createRole(EDIT)
    authHelper.createRole(ADMIN)
    authHelper.createRole(SCAN)

    val createAndLogin = this.createAndLogin(authHelper)

    return DefaultUsers(
      allRolesUser = createAndLogin("all@gmail.com", listOf(EDIT, SCAN, ADMIN)),
      adminOnlyUser = createAndLogin("admin@gmail.com", listOf(ADMIN)),
      scanOnlyUser = createAndLogin("scan@gmail.com", listOf(SCAN)),
      editOnlyUser = createAndLogin("edit@gmail.com", listOf(EDIT)),
      noRolesUser = createAndLogin("no@gmail.com", listOf()))
  }

  private fun createAndLogin(
    authHelper: AuthenticationHelper
  ): (String, List<String>) -> AuthenticationHelper.TestUserWithToken = { name, roles ->
    authHelper.createUser(name, roles + AuthenticationHelper.ACCESS_ROLE).let {
      authHelper.login(it)
    }
  }
}
