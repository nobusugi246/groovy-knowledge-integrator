package gki.chat

import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo

@Slf4j
class ChatController {

  def chatService
  def chatBotDefaultService
  def feedCrawlerService

  def jsonSlurper = new JsonSlurper()
  
  def index() { }

  @MessageMapping("/addUser")
  protected void addUser(ChatMessage message) {
    log.info "addUser: ${message}"
    chatService.addUser(message.username, message.chatroom)

    chatService.sendLog(message)
    chatService.sendUserList()
  }


  @MessageMapping("/updateUser")
  protected void updateUser(ChatMessage message) {
    log.info "updateUser: ${message}"
    chatService.addUser(message.username, message.chatroom)

    chatService.sendUserList()
  }


  @MessageMapping("/log")
  protected String sendLog(ChatMessage message) {
    log.info "log: ${message}"
    chatService.sendLog(message)
  }

  
  @MessageMapping("/message")
  protected String receiveMessage(ChatMessage message) {
    log.info "message: ${message}"
    chatService.receiveMessage(message)
  }
  

  @MessageMapping("/heartbeat")
  protected String heartbeatCount(ChatMessage message) {
    log.debug "heartbeat: ${message}"
    chatService.heartbeatCount(message)
  }


  def webhook() {
    log.info 'webhook called.'

    log.info "${params}"
    if( params.payload ) {
      def payload = jsonSlurper.parseText(params.payload)
      chatBotDefaultService.webhook(payload)
    }

    render ''
  }
}
