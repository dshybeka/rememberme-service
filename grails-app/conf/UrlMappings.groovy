class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/doCall"(controller: "imageApi", action: "doPostCall")

        "/uploadPhoto"(controller: "photo", action: "save")

        "/"(view:"/index")

        "500"(view:'/error')
	}
}
