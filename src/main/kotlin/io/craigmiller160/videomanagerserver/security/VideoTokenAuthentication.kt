package io.craigmiller160.videomanagerserver.security

import io.craigmiller160.videomanagerserver.security.tokenprovider.TokenConstants
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class VideoTokenAuthentication(private val userDetails: UserDetails, val claims: Map<String, Any>) :
  Authentication {
  private var innerIsAuth: Boolean = true

  val filePath: String = claims[TokenConstants.CLAIM_FILE_PATH] as String

  override fun getName(): String = userDetails.username
  override fun getCredentials(): Any = ""
  override fun getDetails(): Any = userDetails
  override fun getPrincipal(): Any = userDetails
  override fun isAuthenticated(): Boolean = innerIsAuth
  override fun setAuthenticated(isAuthenticated: Boolean) {
    this.innerIsAuth = isAuthenticated
  }
  override fun getAuthorities(): MutableCollection<out GrantedAuthority> = userDetails.authorities
}
