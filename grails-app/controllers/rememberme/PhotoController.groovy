package rememberme

import grails.plugin.springsecurity.annotation.Secured

@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class PhotoController {

  static allowedMethods = [save: "POST", check: "GET"]

  @Secured(['ROLE_USER'])
  def check() {

    log.info "Server is really up"

    render "Server up"
  }


  def check2() {

    log.info "Server is really up " + request.JSON

    render "Server up " + params
  }

  def save() {

    log.info "params " + params

    def img = params.img

    render "text"
  }
}
