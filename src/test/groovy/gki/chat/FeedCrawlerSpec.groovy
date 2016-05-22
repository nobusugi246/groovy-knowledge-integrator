package gki.chat

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(FeedCrawler)
class FeedCrawlerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test default constructor"() {
        when:
        def feed = new FeedCrawler()

        then:
        feed.name == ''
        feed.url == ''
        feed.chatroom == ''
        feed.lastFeed == ''
        feed.interval == 30
        feed.countdown == 0
        feed.enabled == true
    }

    void "test map constructor"() {
        when:
        def feed = new FeedCrawler(name: '√①№〜㈱ⅲ', url: 'http://√①№〜㈱ⅲ',
                                   chatroom: '髙低薫', lastFeed: '2010-10-10T11:22:33Z',
                                   interval: 3, countdown: 2, enabled: false)

        then:
        feed.name == '√①№〜㈱ⅲ'
        feed.url == 'http://√①№〜㈱ⅲ'
        feed.chatroom == '髙低薫'
        feed.lastFeed == '2010-10-10T11:22:33Z'
        feed.interval == 3
        feed.countdown == 2
        feed.enabled == false
    }
}
