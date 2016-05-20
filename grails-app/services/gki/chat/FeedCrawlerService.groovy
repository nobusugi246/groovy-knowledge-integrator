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
  def updateCrawlers() {
    log.info 'update crawler config...'
  }
}
