class UrlMappings {

  static mappings = {
    "/$controller/$action?/$id?(.$format)?"{ constraints { // apply constraints here
      } }

    "/doCall"(controller: "imageApi", action: "doPostCall")

    "/"(view:"/index")

    "500"(view:'/error')

    "/login/$action?"(controller: "login")
    "/logout/$action?"(controller: "logout")

    "/api/registration"(controller: "registration", action: "register")

    // user photos mapping
    "/user/${userId}/photo"(controller: "photo") {
      action = [POST:"save", GET:"getUserPhotos"]
    }
    "/user/${userId}/photo/${photoId}"(controller: "photo", action: "getPhoto") {
      action = [GET:"getPhoto"]
    }
  }
}
