import gki.chat.ChatRoom
import gki.chat.FeedCrawler
import gki.chat.WebHook
import groovy.util.logging.Slf4j

@Slf4j
class BootStrap {
  def init = { servletContext ->
    if ( !ChatRoom.get(1) ) {
      new ChatRoom(name: "Entrance").save()
    }

    try {
      def confFile = new File('ChatService.conf')
      def conf = new ConfigSlurper().parse(confFile.getText("UTF-8"))

      conf.rooms.each { room ->
        if( !ChatRoom.findByName(room) ){
          new ChatRoom(name: room).save()
        }
      }

      conf.feeds.each { feed ->
        if( !FeedCrawler.findByUrl(feed[1]) ){
          new FeedCrawler(name: feed[0], url: feed[1], chatroom: feed[2],
                          interval: feed[3], enabled: feed[4]).save()
        }
      }

      conf.hooks.each { hook ->
        if( !WebHook.findByHookFrom(hook[1]) ){
          new WebHook(hookName: hook[0], hookFrom: hook[1],
                      chatroom: hook[2], enabled: hook[3]).save()
        }
      }
    } catch (e) {
      log.info e.message
    }
  }

  def destroy = {
  }
}
