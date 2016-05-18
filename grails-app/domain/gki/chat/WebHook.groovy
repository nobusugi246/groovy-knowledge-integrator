package gki.chat

import groovy.transform.ToString

@ToString(includeNames=true)
class WebHook {
  String hookname = ''
  String hookfrom = ''
  boolean enabled = true

  static constraints = {
    hookname blank: false, editable: true
    hookfrom blank: false, editable: true
    enabled editable: true
  }
}
