package rememberme

class PhotoController {

  static allowedMethods = [save: "POST", check: "GET"]

  def check() {


    log.info "Server is really up"

    render "Server up"
  }

  def save() {

    log.info "params " + params

    def img = params.img

    render "text"
  }
}
