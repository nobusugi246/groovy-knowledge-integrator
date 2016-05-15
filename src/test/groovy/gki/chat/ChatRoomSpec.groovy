package gki.chat

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(ChatRoom)
class ChatRoomSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test default constructor"() {
        when:
        def room = new ChatRoom()

        then:
        room.name == ''
        room.created ==~ /\d{4}-\d{2}-\d{2}/
    }

    void "test map constructor"() {
        when:
        def room = new ChatRoom(name: '髙低薫')

        then:
        room.name == '髙低薫'
        room.created ==~ /\d{4}-\d{2}-\d{2}/
    }
}
