package gki.chat

import grails.converters.JSON
import groovy.util.logging.Slf4j
import grails.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate

@Slf4j
@Transactional
class ChatService {

  SimpMessagingTemplate brokerMessagingTemplate

  String addUser(String name) {
    String result = "${name} was not added."

    if( !ChatUser.findByUsername(name) ){
      new ChatUser(username: name, role: "User").save()
      result = "${name} was added."
    }
    
    return result
  }


  String receiveMessage(ChatMessage message) {
    message.save()

    String to = "/topic/${message.sendto}"
    JSON msg = message as JSON
    
    brokerMessagingTemplate.convertAndSend to, msg.toString()
    return 'done.'
  }
}
