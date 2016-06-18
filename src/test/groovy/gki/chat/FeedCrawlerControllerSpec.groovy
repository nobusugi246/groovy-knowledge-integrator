package gki.chat

import grails.test.mixin.*
import spock.lang.*

@TestFor(FeedCrawlerController)
@Mock(FeedCrawler)
class FeedCrawlerControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params['name'] = 'abc123'
        params['url'] = 'http://def456.com'
        params['chatroom'] = 'ghi789'
        params['lastFeed'] = '2010-10-10T11:22:33Z'
        params['interval'] = 30
        params['countdown'] = 3
        params['enabled'] = true
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.feedCrawlerList
            model.feedCrawlerCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.feedCrawler!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def feedCrawler = new FeedCrawler()
            feedCrawler.validate()
            controller.save(feedCrawler)

        then:"The create view is rendered again with the correct model"
            model.feedCrawler!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            feedCrawler = new FeedCrawler(params)

            controller.save(feedCrawler)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/feedCrawler/show/1'
            controller.flash.message != null
            FeedCrawler.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def feedCrawler = new FeedCrawler(params)
            controller.show(feedCrawler)

        then:"A model is populated containing the domain instance"
            model.feedCrawler == feedCrawler
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def feedCrawler = new FeedCrawler(params)
            controller.edit(feedCrawler)

        then:"A model is populated containing the domain instance"
            model.feedCrawler == feedCrawler
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/feedCrawler/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def feedCrawler = new FeedCrawler()
            feedCrawler.validate()
            controller.update(feedCrawler)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.feedCrawler == feedCrawler

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            feedCrawler = new FeedCrawler(params).save(flush: true)
            controller.update(feedCrawler)

        then:"A redirect is issued to the show action"
            feedCrawler != null
            response.redirectedUrl == "/feedCrawler/show/$feedCrawler.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/feedCrawler/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def feedCrawler = new FeedCrawler(params).save(flush: true)

        then:"It exists"
            FeedCrawler.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(feedCrawler)

        then:"The instance is deleted"
            FeedCrawler.count() == 0
            response.redirectedUrl == '/feedCrawler/index'
            flash.message != null
    }
}
