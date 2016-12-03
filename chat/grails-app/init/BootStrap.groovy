import gki.chat.ChatRoom
import gki.chat.FeedCrawler
import gki.chat.WebHook
import gki.chat.Jenkins
import groovy.util.logging.Slf4j

@Slf4j
class BootStrap {
  def init = { servletContext ->
    log.info 'started...'

    if ( !ChatRoom.get(1) ) {
      new ChatRoom(name: "Entrance").save()
      log.info "new ChatRoom: Entrance"
    }

    try {
      def confFile = new File('ChatService.conf')
      def conf = new ConfigSlurper().parse(confFile.getText("UTF-8"))

      conf.rooms.each { room ->
        if( !ChatRoom.findByName(room) ){
          new ChatRoom(name: room).save()
          log.info "new ChatRoom: ${room}"
        }
      }

      conf.feeds.each { feed ->
        if( !FeedCrawler.findByUrl(feed[1]) ){
          new FeedCrawler(name: feed[0], url: feed[1], chatroom: feed[2],
                          interval: feed[3], enabled: feed[4]).save()
          log.info "new FeedCrawler: ${feed[0]}"
        }
      }

      conf.hooks.each { hook ->
        if( !WebHook.findByHookFrom(hook[1]) ){
          new WebHook(hookName: hook[0], hookFrom: hook[1],
                      chatroom: hook[2], enabled: hook[3]).save()
          log.info "new WebHook: ${hook[0]}"
        }
      }

      conf.jenkins.each { jenkins ->
        if( !Jenkins.findByName(jenkins[0]) ){
          new Jenkins(name: jenkins[0], url: jenkins[1],
                      username: jenkins[2], password: jenkins[3],
                      created: jenkins[4], enabled: jenkins[5]).save()
          log.info "new Jenkins: ${jenkins[0]}"
        }
      }
    log.info 'done'
    } catch (e) {
      log.info e.message
    }
  }

  def destroy = {
  }
}
