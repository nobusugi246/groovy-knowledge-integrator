package gki.chat

import groovy.transform.ToString

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ToString(includeNames=true)
class ChatUser {
  String username = ''
  String password = ''
  String role = ''
  String created = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  long chatroom = 0
  long heartbeatCount = 0
  byte[] iconImage
  boolean enabled = true
  
  static constraints = {
    username blank: false, editable: true
    password size: 5..15, editable: false, nullable: true
    role inList: ["Admin", "User"]
    chatroom editable: false
    created editable: false
    heartbeatCount editable: false, display: false
    iconImage maxSize: 1024000, nullable: true, editable: true, blank: true
    enabled editable: true
  }
}
