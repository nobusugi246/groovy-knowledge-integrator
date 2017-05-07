package gki.chat

import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Slf4j
@Transactional
class ChatBotServerService {
    RestTemplate botServer

    ChatBotServerService(RestTemplateBuilder builder){
        this.botServer = builder
                .setConnectTimeout(2000)
                .setReadTimeout(2000).build()
    }

    def sendMessage(ChatMessage message){
        ChatBotServer.findAllWhere(enabled: true).each { target ->
            log.info "${target.name}: ${target.uri}"
            try {
                botServer.getForEntity target.uri, String, (message as JSON).toString()
            } catch (e) {
                log.info e.message
            }
        }
    }
}
