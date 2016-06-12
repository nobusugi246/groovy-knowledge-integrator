package gki.chat

import groovy.transform.ToString

@ToString(includeNames=true)
class ChatServiceConf {
  def rooms = []
  def feeds = []
  def hooks = []
  def jenkins = []

  def ChatServiceConf() {
    this.rooms = ChatRoom.list().collect { room ->
      room.name
    }

    this.feeds = FeedCrawler.list().collect { feed ->
      [feed.name, feed.url, feed.chatroom, feed.interval, feed.enabled]
    }

    this.hooks = WebHook.list().collect { hook ->
      [hook.hookName, hook.hookFrom, hook.chatroom, hook.enabled]
    }

    this.jenkins = Jenkins.list().collect { jenkins ->
      [jenkins.name, jenkins.url, jenkins.username, jenkins.password,
       jenkins.created, jenkins.enabled]
    }
  }

  
  def asConf(){
    def conf = new ConfigSlurper().parse('''
        rooms = []
        feeds = []
        hooks = []
        jenkins = []
    ''')

    conf.rooms = rooms
    conf.feeds = feeds
    conf.hooks = hooks
    conf.jenkins = jenkins

    return conf
  }
}
