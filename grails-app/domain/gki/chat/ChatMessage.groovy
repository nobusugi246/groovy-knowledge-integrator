package gki.chat

import groovy.transform.ToString

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ToString(includeNames=true)
class ChatMessage {
  String status
  String sendto
  String text
  String username

  String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

  static constraints = {
    text editable: true
    status editable: true, nullable: true
    sendto editable: true
    username editable: true
    date editable: false
    time editable: false
  }
}
