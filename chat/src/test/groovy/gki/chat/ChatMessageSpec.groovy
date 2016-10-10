package gki.chat

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ChatMessage)
class ChatMessageSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test default constructor"() {
        when:
        def message = new ChatMessage()

        then:
        message.status == ''
        message.chatroom == ''
        message.text == ''
        message.username == ''
        message.date ==~ /\d{4}-\d{2}-\d{2}/
        message.time ==~ /\d{2}:\d{2}:\d{2}/
    }

    void "test map constructor"() {
        when:
        def message = new ChatMessage(status: 'abc123', chatroom: 'def456', text: '√①№〜㈱ⅲ', username: '髙低薫')

        then:
        message.status == 'abc123'
        message.chatroom == 'def456'
        message.text == '√①№〜㈱ⅲ'
        message.username == '髙低薫'
        message.date ==~ /\d{4}-\d{2}-\d{2}/
        message.time ==~ /\d{2}:\d{2}:\d{2}/
    }
}
