package gki.chat

import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper
import grails.converters.JSON

import org.springframework.messaging.handler.annotation.MessageMapping

@Slf4j
class ChatController {

  def chatService
  def chatBotDefaultService
  def feedCrawlerService

  String thisUserName = ''
  
  def jsonSlurper = new JsonSlurper()
  
  def index() {}

  @MessageMapping("/addUser")
  protected void addUser(ChatMessage message) {
    log.info "addUser: ${message}"
    thisUserName = message.username
    chatService.addUser(message.username, message.chatroom)

    chatService.sendLog(message)
    chatService.sendUserList()
  }


  @MessageMapping("/updateUser")
  protected void updateUser(ChatMessage message) {
    log.info "updateUser: ${message}"
    thisUserName = message.username
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
  

  @MessageMapping("/tempMessage")
  protected String receiveTempMessage(ChatMessage message) {
    chatService.receiveTempMessage(message)
  }
  

  @MessageMapping("/heartbeat")
  protected String heartbeatCount(ChatMessage message) {
    log.debug "heartbeat: ${message}"
    chatService.heartbeatCount(message)
  }


  def countMessages() {
    def day = params.day.replace('/', '-')
    def counted = ChatMessage.countByChatroomAndDate(params.room, day)
    //    log.info "room: ${params.room}, day: ${day}, count: ${counted}"

    def result = [:]
    result << ['count': counted]
    result << ['day': params.day]
    
    render result as JSON
  }
  

  def export() {
    def tempFile = File.createTempFile("ChatService", ".conf");
    log.info tempFile.getPath()
    
    def result = new ChatServiceConf()

    tempFile.withWriter { writer ->
      result.asConf().writeTo(writer)
    }
    
    response.setContentType("application/octet-stream")
    response.setHeader("Content-disposition", "filename=ChatService.conf")
    response.outputStream << tempFile.text
    response.outputStream.flush()

    tempFile.deleteOnExit()
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


  def uploadFile() {
    def userName = params.upload[16..-1]
    log.info "params.upload: _${userName}_"
    log.info 'file upload...'

    def file = request.getFile('uploadFile')
    def fileSep = (file.originalFilename).split(/\./)
    log.info "fileSep: ${fileSep}"
    def fileExt = fileSep.length > 0 ? fileSep[-1] : fileSep
    log.info "file.originalFilename: ${file.originalFilename}"
    log.info "fileExt: ${fileExt}"
    if ( file.originalFilename ) {
      chatService.setUserIconImage(userName, file.getBytes(), fileExt)
      render view: 'index'
    } else {
      chatService.deleteUserIconImage(userName)
      render view: 'index'
    }
  }

  
  def icon() {
    //    log.info "params: ${params}"
    def user = ChatUser.findByUsername(params.name)

    if (!user) {
      render ''
    } else {
      response.setContentType("application/octet-stream")
      response.setHeader("Content-disposition", "filename=${user.username}.${user.iconImageType}")
      response.outputStream << user.iconImage
      response.outputStream.flush()
    }
  }
}
