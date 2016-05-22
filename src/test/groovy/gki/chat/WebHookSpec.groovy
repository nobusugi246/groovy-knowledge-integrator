package gki.chat

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(WebHook)
class WebHookSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test default constructor"() {
        when:
        def hook = new WebHook()

        then:
        hook.hookName == ''
        hook.hookFrom == ''
        hook.chatroom == ''
        hook.enabled == true
    }

    void "test map constructor"() {
        when:
        def hook = new WebHook(hookName: '√①№〜㈱ⅲ', hookFrom: 'http://髙低薫',
                               chatroom: 'abcdef123456', enabled: false)

        then:
        hook.hookName == '√①№〜㈱ⅲ'
        hook.hookFrom == 'http://髙低薫'
        hook.chatroom == 'abcdef123456'
        hook.enabled == false
    }
}
