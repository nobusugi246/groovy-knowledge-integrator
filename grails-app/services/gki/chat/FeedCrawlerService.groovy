package gki.chat

import grails.transaction.Transactional
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import java.text.SimpleDateFormat

@Slf4j
@Transactional
class FeedCrawlerService {
  static lazyInit = false
   
  def chatBotDefaultService

  def atomDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
  def rssDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ")

  @Scheduled(fixedRate=60000L)
  void updateCrawlers() {
    log.info 'update crawlers ...'

    def fcList = FeedCrawler.findAllWhere(enabled: true)

    fcList.each { crawler ->
      if( !crawler.countdown ) {
        def url = crawler.url.toURL()
        def content
        def feed
        
        try {
          content = url.getText(connectTimeout: 10000, readTimeout: 10000,
                                useCaches: false, allowUserInteraction: false,
                                requestProperties: ['User-Agent': 'groovy Knowledge Integrator'])

          feed = new XmlSlurper().parseText(content)
        } catch (e) {
          println e.stackTrace
          crawler = null
        }

        def feedTimestamp
        if( feed.updated.text() ) feedTimestamp = feed.updated.text()
        else if( feed.channel.lastBuildDate.text() ) feedTimestamp = feed.channel.lastBuildDate.text()
        else if( feed.channel.date.text() ) feedTimestamp = feed.channel.date.text()

        if( !crawler.lastFeed ) {
          sendMessageByChatroom crawler.chatroom,
                                "新しい Feedが登録されました。 ${crawler.url}",
                                crawler.name
          sendMessageByChatroom crawler.chatroom,
                                "最新のメッセージは以下です。",
                                crawler.name
        }

        def sendFlag = true
        def nextLastFeed = ''
        if( feed.entry.title.text() ) {
          // atom
          feed.entry.each { fd ->
            def time = fd.updated.text()
            if( !nextLastFeed ) nextLastFeed = fd.link.@href.text()

            if( !crawler.lastFeed ) crawler.lastFeed = nextLastFeed
            else if( crawler.lastFeed == fd.link.@href.text() ) sendFlag = false
            if (sendFlag) {
              def reply = "<a href='${fd.link.@href.text()}'>${fd.title.text()}</a> &nbsp; ${time}"
              sendMessageByChatroom crawler.chatroom, reply, crawler.name
            }
            if( crawler.lastFeed == fd.link.@href.text() ) sendFlag = false
          }
        } else if( feed.channel.item.title.text() ) {
          // rss
          feed.channel.item.each { fd ->
            def time = fd.pubDate.text()
            if( !nextLastFeed ) nextLastFeed = fd.link.text()

            if( !crawler.lastFeed ) crawler.lastFeed = nextLastFeed
            else if( crawler.lastFeed == fd.link.text() ) sendFlag = false
            if (sendFlag) {
              def reply = "<a href='${fd.link.text()}'>${fd.title.text()}</a> &nbsp; ${time}"
              sendMessageByChatroom crawler.chatroom, reply, crawler.name
            }
            if( crawler.lastFeed == fd.link.text() ) sendFlag = false
          }
        } else if( feed.channel.title.text() ) {
          // rdf
          feed.item.each { fd ->
            def time = fd.date.text()
            if( !nextLastFeed ) nextLastFeed = fd.link.text()

            if( !crawler.lastFeed ) crawler.lastFeed = nextLastFeed
            else if( crawler.lastFeed == fd.link.text() ) sendFlag = false
            if (sendFlag) {
              def reply = "<a href='${fd.link.text()}'>${fd.title.text()}</a> &nbsp; ${time}"
              sendMessageByChatroom crawler.chatroom, reply, crawler.name
            }
            if( crawler.lastFeed == fd.link.text() ) sendFlag = false
          }
        }

        if( crawler ) {
          crawler.lastFeed = nextLastFeed
          crawler.countdown = crawler.interval
          crawler.save()
        }
      }

      if( crawler ) {
        crawler.countdown--
        crawler.save()
      }
    }
    
    log.info 'update crawlers ... done'
  }


  def sendMessageByChatroom(String chatroomName, String msg, String name){
    def sendToList = ChatRoom.findByName(chatroomName)
    if( !sendToList ) {
      sendToList = ChatRoom.findAll()
    }

    def reclist = sendToList.collect {
      it.id as String
    }
            
    reclist.each {
      chatBotDefaultService.replyMessage it, msg, true, name
    }
  }
}
