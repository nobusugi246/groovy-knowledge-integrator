package gki.chat

import groovy.util.logging.Slf4j

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

@Slf4j
class ChatController {

  def chatService
  
  def index() { }

  @MessageMapping("/addUser")
  @SendTo("/topic/result")
  protected String addUser(String name) {
    log.info("addUser: ${name}")
    return chatService.addUser(name)
  }
  
  @MessageMapping("/message")
  @SendTo("/topic/result")
  protected String receiveMessage(ChatMessage message) {
    log.info("message: ${message}")
    return chatService.receiveMessage(message)
  }
  
}
