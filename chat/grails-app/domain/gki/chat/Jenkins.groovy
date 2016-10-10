package gki.chat

import groovy.transform.ToString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ToString(includeNames=true)
class Jenkins {
  String name = ''
  String url = ''
  String username = ''
  String password = ''
  String created = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  boolean enabled = true

  
  static constraints = {
    name editable: true
    url blank: false, editable: true, maxSize: 1024
    username blank: true, nullable: true, editable: true
    password blank: true, nullable: true, editable: true
    created editable: false
    enabled editable: true
  }
}
