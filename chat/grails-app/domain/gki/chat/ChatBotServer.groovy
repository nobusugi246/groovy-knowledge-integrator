package gki.chat

import groovy.transform.ToString

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ToString(includeNames=true)
class ChatBotServer {
  String name = ''
  String uri = ''
  String created = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  boolean enabled = true
  
  static constraints = {
    name blank: false, editable: true
    uri editable: true
    created editable: false
    enabled editable: true
  }
}
