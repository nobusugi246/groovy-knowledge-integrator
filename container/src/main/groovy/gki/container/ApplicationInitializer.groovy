package gki.container

import gki.container.domain.Bot
import gki.container.domain.BotRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.context.event.EventListener

import java.sql.Timestamp

@Lazy
@Slf4j
@CompileStatic
@Component
class ApplicationInitializer {
    final String starterBotName = 'Starter'

    @Autowired
    BotRepository repository

    ApplicationInitializer(){
        log.info 'ApplicationInitializer created.'
    }

    @EventListener
    void initialize(ApplicationReadyEvent event){
        log.info 'ApplicationReadyEvent catched.'
        if( repository.findByName(starterBotName).size() == 0 ){
            def now = new Timestamp(System.currentTimeMillis())
            def starterBot = new Bot(name: starterBotName,
                    enabled: true,
                    createdBy: 'gKI Bot Container',
                    createdDate: now,
                    script: '')
            log.info starterBot.toString()
            repository.save(starterBot)

            log.info 'Starter Bot was created.'
        }
    }
}
