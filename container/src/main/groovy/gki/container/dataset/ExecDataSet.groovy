package gki.container.dataset

import gki.container.domain.ChatServer
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@Slf4j
@ToString(includeNames=true)
class ExecDataSet {
    Iterable<ChatServer> chatServers
    ChatMessage message
    String name

    void send(String text, String dmtarget = 'null') {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def sendMsg = new ChatMessage(text: text, chatroom: message.chatroom,
                                      username: name, dmtarget: dmtarget)

        def restTemplate = new RestTemplate()
        chatServers.findAll { it.enabled }?.each { server ->
            log.info "${name}: ${text} to ${server.url}"
            HttpEntity<ChatMessage> entity = new HttpEntity<ChatMessage>(sendMsg ,headers)
            restTemplate.postForLocation "${server.url}/chat/chatMessage", entity
        }
    }

    void reply(String text) {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def sendMsg = new ChatMessage(text: text, chatroom: message.chatroom,
                                      username: name, dmtarget: message.username)

        def restTemplate = new RestTemplate()
        chatServers.findAll { it.enabled }?.each { server ->
            log.info "${name}: ${text} to ${server.url}"
            HttpEntity<ChatMessage> entity = new HttpEntity<ChatMessage>(sendMsg ,headers)
            restTemplate.postForLocation "${server.url}/chat/chatMessage", entity
        }
    }
}
