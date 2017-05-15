package gki.container.service

import gki.container.common.ChatMessage
import gki.container.domain.Bot
import gki.container.domain.BotRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

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
        log.info "message: ${message.date} ${message.time}, ${message.username}"

        repository.findAll().each { bot ->
            if( bot.enabled ){
                log.info "bot: ${bot.name}"
                if( message.username == bot.name) return

                if( !bot.acceptAll && (message.dmtarget != bot.name) ) return

                def script = URLDecoder.decode(new String(bot.script.decodeBase64(), 'UTF-8'), 'UTF-8')
                def result = Eval.me('message', message, script)
                if( result == '' ) return

                HttpHeaders headers = new HttpHeaders()
                headers.setContentType(MediaType.APPLICATION_JSON)

                def repmsg = new ChatMessage(text: result.toString(), chatroom: message.chatroom,
                                             username: bot.name)

                def restTemplate = new RestTemplate()
                HttpEntity<ChatMessage> entity = new HttpEntity<ChatMessage>(repmsg ,headers)
                restTemplate.postForLocation 'http://localhost:8080/chat/chatMessage', entity
            }
        }
    }
}
