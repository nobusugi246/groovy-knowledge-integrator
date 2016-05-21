package gki.chat

import grails.transaction.Transactional
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled

@Slf4j
@Transactional
class FeedCrawlerService {

  @Scheduled(fixedRate=60000L)
  void updateCrawlers() {
    log.info 'update crawler ...'

    def fcList = FeedCrawler.findAllWhere(enabled: true)

    fcList.each { crawler ->
      if( !crawler.countdown ) {
        def url = crawler.url.toURL()
        def content = url.getText(connectTimeout: 10000, readTimeout: 10000,
                                  useCaches: false, allowUserInteraction: false,
                                  requestProperties: ['User-Agent': 'groovy Knowledge Integrator'])

        def feed = new XmlSlurper().parseText(content)

        println content.length()
        println feed.title
        println feed.updated
        println '----------'

        feed.entry.each {
          println it.title
          println it.updated
          println it.link.@href.text()
        }
        
        crawler.countdown = crawler.cycle
        crawler.save()
      }

      crawler.countdown--
      crawler.save()
    }
    
    log.info 'update crawler ... done'
  }
}
