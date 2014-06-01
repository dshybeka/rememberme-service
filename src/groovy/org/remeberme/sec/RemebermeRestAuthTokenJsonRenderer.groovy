package org.remeberme.sec

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils

import org.pac4j.core.profile.CommonProfile
import org.rememberme.security.SecUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import com.odobo.grails.plugin.springsecurity.rest.RestAuthenticationToken
import com.odobo.grails.plugin.springsecurity.rest.oauth.OauthUser
import com.odobo.grails.plugin.springsecurity.rest.token.rendering.RestAuthenticationTokenJsonRenderer

@Component("restAuthenticationTokenJsonRenderer")
class RemebermeRestAuthTokenJsonRenderer implements RestAuthenticationTokenJsonRenderer{

  @Override
  public String generateJson(RestAuthenticationToken restAuthenticationToken) {

    def result = [:]

    Assert.isInstanceOf(UserDetails, restAuthenticationToken.principal, "A UserDetails implementation is required")
    UserDetails userDetails = restAuthenticationToken.principal

    SecUser user = SecUser.findByUsername(userDetails.username)

    if (user) {

      def conf = SpringSecurityUtils.securityConfig

      String usernameProperty = conf.rest.token.rendering.usernamePropertyName
      String tokenProperty = conf.rest.token.rendering.tokenPropertyName
      String authoritiesProperty = conf.rest.token.rendering.authoritiesPropertyName

      result["$usernameProperty"] = userDetails.username
      result["$tokenProperty"] = restAuthenticationToken.tokenValue
      result["$authoritiesProperty"] = userDetails.authorities.collect {GrantedAuthority role -> role.authority }
      result["userId"] = user.id

      if (userDetails instanceof OauthUser) {
        CommonProfile profile = (userDetails as OauthUser).userProfile
        result.with {
          email = profile.email
          displayName = profile.displayName
        }
      }
    }

    def jsonResult = result as JSON

    log.debug "Generated JSON:\n ${jsonResult.toString(true)}"

    return jsonResult.toString()
  }
}
