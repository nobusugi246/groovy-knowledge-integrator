package mmbot.container

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Lazy
@Slf4j
@CompileStatic
@Component
class ApplicationInitializer {
    ApplicationInitializer(){
        log.info 'ApplicationInitializer created.'
    }

    @EventListener
    void initialize(ApplicationReadyEvent event){
        log.info 'ApplicationReadyEvent catched.'
    }
}
