package gki.chat

import groovy.transform.ToString

@ToString(includeNames=true)
class WebHook {
  String hookname = ''
  String hookfrom = ''
  String chatroom = ''
  boolean enabled = true

  static constraints = {
    hookname blank: false, editable: true
    hookfrom blank: false, editable: true
    chatroom blank: true, editable: true, nullable: true
    enabled editable: true
  }
}
