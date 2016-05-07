package gki.chat

import grails.converters.JSON
import groovy.util.logging.Slf4j
import grails.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled

@Slf4j
@Transactional
class ChatService {

  SimpMessagingTemplate brokerMessagingTemplate

  void addUser(String name, String sendto) {
    def user = ChatUser.findByUsername(name)
    if( user ) {
      user.enabled = true
      user.save()
    } else {
      new ChatUser(username: name, role: "User", chatroom: sendto as long).save()
      log.info "${name} was added."
      return
    }
  }


  void receiveMessage(ChatMessage message) {
    message.save()
    addUser(message.username, message.sendto)

    String to = "/topic/${message.sendto}"
    String msg = (message as JSON).toString()
    
    brokerMessagingTemplate.convertAndSend to, msg
  }


  void sendTodayLog(ChatMessage message) {
    String to = "/topic/${message.username}"

    def log = ChatMessage.findAllByDate(message.date, [sort: "id"])

    log.findAll {
      it.sendto == message.sendto
    }.each {
      String msg = (it as JSON).toString()
      brokerMessagingTemplate.convertAndSend to, msg
    }
  }

  
  void heartbeatCount(ChatMessage message) {
    def user = ChatUser.findByUsername(message.username)
    if(user) {
      user.heartbeatCount++
    }
  }


  void sendUserList() {
    def users = ChatUser.findAll()
    def userList = users.findAll {
      it.enabled == true
    }.collect {
      ['id': it.id, 'username': it.username]
    }

    users.each {
      String to = "/topic/${it.username}"
      String msg = (userList as JSON).toString()
      brokerMessagingTemplate.convertAndSend to, msg
    }
  }
  

  @Scheduled(fixedRate=10000L)
  void updateUserConnection() {
    log.info 'executed...'

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

