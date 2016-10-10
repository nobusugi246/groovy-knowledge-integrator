package gki.chat

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Jenkins)
class JenkinsSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test default constructor"() {
        when:
        def js = new Jenkins()
        
        then:
        js.name == ''
        js.url == ''
        js.username == ''
        js.password == ''
        js.created ==~ /[0-9]{4}-[0-9]{2}-[0-9]{2}/
        js.enabled == true
    }

    void "test map constructor"() {
        when:
        def js = new Jenkins(name: '√①№〜㈱ⅲ', url: 'http://√①№〜㈱ⅲ',
                             username: '髙低薫', password: 'abc012', enabled: false)

        then:
        js.name == '√①№〜㈱ⅲ'
        js.url == 'http://√①№〜㈱ⅲ'
        js.username == '髙低薫'
        js.password == 'abc012'
        js.created ==~ /[0-9]{4}-[0-9]{2}-[0-9]{2}/
        js.enabled == false
    }
}
