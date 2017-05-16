package gki.chat

import groovy.transform.ToString

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ToString(includeNames=true)
class ChatMessage {
  String status = ''
  String chatroom = ''
  String text = ''
  String username = ''
  String dmtarget = ''

  String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

  static constraints = {
    username editable: false
    chatroom editable: false
    text editable: false, maxSize: 1024
    status editable: true, nullable: true, display: false
    dmtarget editable: false, nullable: true
    date editable: false
    time editable: false
  }
}
