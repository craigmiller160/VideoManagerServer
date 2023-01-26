package io.craigmiller160.videomanagerserver.test_util

import io.craigmiller160.testcontainers.common.core.AuthenticationHelper

data class DefaultUsers(
  val allRolesUser: AuthenticationHelper.TestUserWithToken,
  val adminOnlyUser: AuthenticationHelper.TestUserWithToken,
  val scanOnlyUser: AuthenticationHelper.TestUserWithToken,
  val editOnlyUser: AuthenticationHelper.TestUserWithToken,
  val noRolesUser: AuthenticationHelper.TestUserWithToken
)
