package gki.container.Controller

import gki.container.domain.Bot
import gki.container.domain.BotRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.sql.Timestamp

@Slf4j
@CompileStatic
@RestController
class DefaultController {

    @Autowired
    BotRepository repository

    @PostMapping("/create")
    String createBot(@RequestParam(name = "name")String name,
                     @RequestParam(name = "from")String from,
                     @RequestParam(name = "user")String userName){
        log.info "name: ${name}, from: ${from}, user: ${userName}"

        if( repository.findByName(name).size() > 0 ){
            return "Same name bot already exists."
        }

        def botFrom = repository.findByName(from)
        if( botFrom.size() == 0 ){
            return "Bot for quoting is not exist."
        }

        def newBot = Bot.builder()
                .name(name)
                .createdBy(userName)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .script(botFrom[0].script)
                .build()
        repository.save(newBot)

        return "Created."
    }
}
