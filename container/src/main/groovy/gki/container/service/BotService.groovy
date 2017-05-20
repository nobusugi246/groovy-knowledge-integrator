package gki.container.service

import gki.container.dataset.ChatMessage
import gki.container.dataset.ExecDataSet
import gki.container.dataset.TestDataSet
import gki.container.domain.Bot
import gki.container.domain.BotRepository
import gki.container.domain.ChatServer
import gki.container.domain.ChatServerRepository
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
    BotRepository botRepository

    @Autowired
    ChatServerRepository chatServerRepository

    String createBot(String name, String from, String userName){
        log.info "name: ${name}, from: ${from}, user: ${userName}"

        if( botRepository.findByName(name).size() > 0 ){
            return "Same name bot already exists."
        }

        def botFrom = botRepository.findByName(from)
        if( botFrom.size() == 0 ){
            return "Bot for quoting is not exist."
        }

        def newBot = new Bot(
                name: name,
                createdBy: userName,
                createdDate: new Timestamp(System.currentTimeMillis()),
                script: botFrom[0].script)
        botRepository.save(newBot)
        return "Created."
    }

    @Async
    void execBotScript(ChatMessage message){
        log.info "message: ${message.date} ${message.time}, ${message.username}"

        botRepository.findAll().each { bot ->
            if( bot.enabled ){
                log.info "- bot: ${bot.name}"
                if( message.username == bot.name) return

                if( !bot.acceptAll && (message.dmtarget != bot.name) ) return

                def script = URLDecoder.decode(new String(bot.script.decodeBase64(), 'UTF-8'), 'UTF-8')
                def chatServers = chatServerRepository.findAll()
                def dataset = new ExecDataSet(chatServers: chatServers, message: message)
                def result = Eval.me('ds', dataset, script)
                if( result == '' ) return

                HttpHeaders headers = new HttpHeaders()
                headers.setContentType(MediaType.APPLICATION_JSON)

                def repmsg = new ChatMessage(text: result.toString(), chatroom: message.chatroom,
                                             username: bot.name, dmtarget: message.username)

                def restTemplate = new RestTemplate()
                HttpEntity<ChatMessage> entity = new HttpEntity<ChatMessage>(repmsg ,headers)
                restTemplate.postForLocation 'http://localhost:8080/chat/chatMessage', entity
            }
        }
    }

    String testBot(TestDataSet dataSet){
        dataSet.script = URLDecoder.decode(new String(dataSet.script.decodeBase64(), 'UTF-8'), 'UTF-8')
        def result = ''
        def message = new ChatMessage(username: 'test', text: dataSet.message,
                                      chatroom: 0, dmtarget: dataSet.botname)

        def chatServers = chatServerRepository.findAll()
        def dataset = new ExecDataSet(chatServers: chatServers, message: message)
        try {
            result = Eval.me('ds', dataset, dataSet.script)
        } catch (e) {
            result = '<strong>Script Error</strong>:<br/>' + e.message.toString()
        }
        return result
    }
}
