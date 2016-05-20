package gki.chat

import groovy.transform.ToString

@ToString(includeNames=true)
class FeedCrawler {
  String name = ''
  String url = ''
  String chatroom = ''
  String lastFeed = ''
  Long cycle = 10
  boolean enabled = true

  static constraints = {
    name blank: false, editable: true
    url blank: false, editable: true
    chatroom blank: true, editable: true, nullable: true
    lastFeed editable: false, maxSize: 10240, blank: true, nullable: true
    cycle editable: true
    enabled editable: true
  }
}
