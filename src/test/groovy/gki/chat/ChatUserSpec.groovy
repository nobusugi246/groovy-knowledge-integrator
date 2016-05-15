package gki.chat

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(ChatUser)
class ChatUserSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test default constructor"() {
        when:
        def user = new ChatUser()

        then:
        user.username == ''
        user.password == ''
        user.role == ''
        user.created ==~ /\d{4}-\d{2}-\d{2}/
        user.enabled == true
        user.chatroom == 0
        user.heartbeatCount == 0
    }

    void "test map constructor"() {
        when:
        def user = new ChatUser(username: '髙低薫', password: 'abc123',
                                role: 'def456', enabled: false)

        then:
        user.username == '髙低薫'
        user.password == 'abc123'
        user.role == 'def456'
        user.created ==~ /\d{4}-\d{2}-\d{2}/
        user.enabled == false
        user.chatroom == 0
        user.heartbeatCount == 0
    }
}
