package gki.chat

import groovy.transform.ToString

@ToString(includeNames=true)
class WebHook {
  String hookName = ''
  String hookFrom = ''
  String chatroom = ''
  boolean enabled = true

  static constraints = {
    hookName blank: false, editable: true
    hookFrom blank: false, editable: true, maxSize: 1024
    chatroom blank: true, editable: true, nullable: true
    enabled editable: true
  }
}
