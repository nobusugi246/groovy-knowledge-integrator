package mmbot.container.controller

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import mmbot.container.dataset.CommandResponse
import mmbot.container.service.AboutSpringService
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import java.util.concurrent.CompletableFuture

@Lazy
@Slf4j
@CompileStatic
@RestController
class DefaultController {
    AboutSpringService aboutSpringService

    DefaultController(AboutSpringService aboutSpringService){
        this.aboutSpringService = aboutSpringService
    }

    @Async
    @GetMapping('/about-spring')
    CompletableFuture<CommandResponse> aboutSpring(){
        return CompletableFuture.completedFuture(aboutSpringService.about())
    }
}
