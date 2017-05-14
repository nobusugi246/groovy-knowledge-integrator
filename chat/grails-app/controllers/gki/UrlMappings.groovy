package gki

class UrlMappings {

    static mappings = {
//        post "/chatMessage"(controller: "chat", action: "chatMessage")

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
