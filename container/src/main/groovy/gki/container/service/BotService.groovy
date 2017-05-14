package gki.container.service

import gki.container.common.ChatMessage
import gki.container.domain.Bot
import gki.container.domain.BotRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

import java.sql.Timestamp

@Slf4j
@CompileStatic
@Service
class BotService {
    @Autowired
    BotRepository repository

    String createBot(String name, String from, String userName){
        log.info "name: ${name}, from: ${from}, user: ${userName}"

        if( repository.findByName(name).size() > 0 ){
            return "Same name bot already exists."
        }

        def botFrom = repository.findByName(from)
        if( botFrom.size() == 0 ){
            return "Bot for quoting is not exist."
        }

        def newBot = new Bot(
                name: name,
                createdBy: userName,
                createdDate: new Timestamp(System.currentTimeMillis()),
                script: botFrom[0].script)
        repository.save(newBot)
        return "Created."
    }

    @Async
    void execBotScript(ChatMessage message){
        log.info message.toString()

        repository.findAll().each { bot ->
            if( bot.enabled ){
                log.info "bot: ${bot.name}"
                if( message.username == bot.name) return

                message.botname = bot.name
                def script = URLDecoder.decode(new String(bot.script.decodeBase64(), 'UTF-8'), 'UTF-8')
                Eval.me('message', message, script)
            }
        }
    }
}
