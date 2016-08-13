package gki.chat

import grails.converters.JSON
import groovy.util.logging.Slf4j
import grails.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled

@Slf4j
@Transactional
class ChatService {
  static lazyInit = false
   
  def chatBotDefaultService
  
  SimpMessagingTemplate brokerMessagingTemplate

  void receiveMessage(ChatMessage message) {
    message.save()
    //    addUser(message.username, message.chatroom)

    String to = "/topic/${message.chatroom}"
    String msg = message as JSON
    
    brokerMessagingTemplate.convertAndSend to, msg

    chatBotDefaultService.defaultHandler(message)
  }


  void receiveTempMessage(ChatMessage message) {
    String to = "/topic/temp/${message.chatroom}"
    String msg = message as JSON
    
    brokerMessagingTemplate.convertAndSend to, msg
  }


  void addUser(String name, String chatroom) {
    def user = ChatUser.findByUsername(name)
    if( user ) {
      user.chatroom = chatroom as long
      user.enabled = true
      user.save()
    } else {
      new ChatUser(username: name, role: "User", chatroom: chatroom as long).save()
      log.info "${name} was added."

      chatBotDefaultService.hello(name)
    }
  }


  void sendLog(ChatMessage message) {
    String to = "/topic/${message.username}"

    def log = ChatMessage.findAllByDate(message.text, [sort: "id"])

    log.findAll {
      it.chatroom == message.chatroom
    }.each {
      String msg = it as JSON
      brokerMessagingTemplate.convertAndSend to, msg
      Thread.sleep(20)
    }

    if (message.text != message.date) {
      String closeTimeMsg = new ChatMessage(status: 'closeTime') as JSON
      brokerMessagingTemplate.convertAndSend to, closeTimeMsg
    }
  }

  
  void heartbeatCount(ChatMessage message) {
    def user = ChatUser.findByUsername(message.username)
    if(user) {
      user.enabled = true
      user.heartbeatCount++
      user.save()
    }
  }


  void sendUserList() {
    def users = ChatUser.findAll()
    def userListActive = users.findAll {
      it.enabled == true
    }

    def chatRooms = ChatRoom.findAll()

    userListActive.each { user ->
      String to = "/topic/${user.username}"

      def userListByChatRoom = userListActive.findAll {
        user.chatroom == it.chatroom
      }.collect {
        ['id': it.id, 'username': it.username]
      }
      String msgUL = [userList: userListByChatRoom] as JSON
      String msgCRL = [chatRoomList: chatRooms, selected: user.chatroom] as JSON
      
      brokerMessagingTemplate.convertAndSend to, msgUL
      brokerMessagingTemplate.convertAndSend to, msgCRL
    }
  }


  void setUserIconImage(String username, byte[] image, String type) {
    log.info "username: ${username}"

    def user = ChatUser.findByUsername(username)
    user.setIconImage image
    user.setIconImageType type
    user.save()
  }
  

  @Scheduled(fixedRate=10000L)
  void updateUserConnection() {
    log.info 'update UserList'

    def users = ChatUser.findAll()
    users.findAll{
      it.heartbeatCount == 0
    }.each {
      it.enabled = false
    }

    users = ChatUser.findAll()
    users.each {
      it.heartbeatCount = 0
    }

    sendUserList()
  }
}

