package gki.chat

import groovy.transform.ToString

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ToString(includeNames=true)
class ChatRoom {
  String name
  String created = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

  static hasMany = [chatUsers: ChatUser]
  
  static constraints = {
    name editable: true
    created editable: true
    chatUsers editable: true
  }
}
