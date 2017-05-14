package gki.container.common

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
@CompileStatic
@ToString(includeNames=true)
class ChatMessage {
    long id
    String chatroom
    String text
    String username
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    String botname
    String dmtarget

    void sendMessage(String message){
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def cm = new ChatMessage(text: message, chatroom: this.chatroom,
                                 username: this.botname)

        def restTemplate = new RestTemplate()
        HttpEntity<ChatMessage> entity = new HttpEntity<ChatMessage>(cm ,headers)
        restTemplate.postForLocation 'http://localhost:8080/chat/chatMessage', entity
//        log.info "${message} send."
    }
}
