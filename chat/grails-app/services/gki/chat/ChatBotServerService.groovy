package gki.chat

import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
                HttpHeaders headers = new HttpHeaders()
                headers.setContentType(MediaType.APPLICATION_JSON)

                HttpEntity<String> entity = new HttpEntity<String>((message as JSON).toString() ,headers)
                botServer.postForLocation "${target.uri}/chatMessage", entity
            } catch (e) {
                log.info e.message
            }
        }
    }
}
