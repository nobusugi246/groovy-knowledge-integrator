package gki.chat

import groovy.transform.ToString

@ToString(includeNames=true)
class FeedCrawler {
  String name = ''
  String url = ''
  String chatroom = ''
  String lastFeed = ''
  Long cycle = 30  // in min.
  Long countdown = 0
  boolean enabled = true

  static constraints = {
    name blank: false, editable: true
    url blank: false, editable: true
    chatroom blank: true, editable: true, nullable: true
    lastFeed editable: false, maxSize: 102400, blank: true, nullable: true, display: false
    cycle editable: true
    countdown editable: false, display: false
    enabled editable: true
  }
}
