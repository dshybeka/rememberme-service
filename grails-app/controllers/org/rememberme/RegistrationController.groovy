package org.rememberme

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.springsecurity.userdetails.GrailsUser

import org.rememberme.security.SecRole
import org.rememberme.security.SecUser
import org.rememberme.security.SecUserSecRole

import com.google.common.collect.Lists
import com.odobo.grails.plugin.springsecurity.rest.token.generation.TokenGenerator
import com.odobo.grails.plugin.springsecurity.rest.token.storage.TokenStorageService

@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class RegistrationController {

  static allowedMethods = [register: "POST"]

  TokenGenerator tokenGenerator
  TokenStorageService tokenStorageService

  def register() {

    def result = [message: "Error while registration, please try again later"]

    String userName = params.username
    String password = params.password

    if (userName && password) {

      SecUser.withTransaction {

        def user = new SecUser(username: userName, password: password)
        if (!user.save(flush: true)) {

          log.warn "Could not save user: ${user.errors}"

          result.success = false
          result.message = "Sorry, user with email '${userName}' already exists"
        } else {

          def roleUser=  SecRole.findByAuthority("ROLE_USER")

          new SecUserSecRole(secUser:user, secRole:roleUser).save()

          String tokenValue = tokenGenerator.generateToken()
          log.debug "Generated token: ${tokenValue}"

          GrailsUser tokenUser = new GrailsUser(userName, password, true, true, true, true, Lists.newArrayList(), 1)

          tokenStorageService.storeToken(tokenValue, tokenUser)

          result.token = tokenValue
          result.success = true
        }
      }
    }

    render result as JSON
  }
}
