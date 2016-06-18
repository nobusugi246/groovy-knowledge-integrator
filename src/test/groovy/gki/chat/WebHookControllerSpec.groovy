package gki.chat

import grails.test.mixin.*
import spock.lang.*

@TestFor(WebHookController)
@Mock(WebHook)
class WebHookControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params['hookName'] = 'abc123'
        params['hookFrom'] = 'http://def456.com'
        params['chatroom'] = 'test'
        params['enabled'] = true
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.webHookList
            model.webHookCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.webHook!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def webHook = new WebHook()
            webHook.validate()
            controller.save(webHook)

        then:"The create view is rendered again with the correct model"
            model.webHook!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            webHook = new WebHook(params)

            controller.save(webHook)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/webHook/show/1'
            controller.flash.message != null
            WebHook.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def webHook = new WebHook(params)
            controller.show(webHook)

        then:"A model is populated containing the domain instance"
            model.webHook == webHook
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def webHook = new WebHook(params)
            controller.edit(webHook)

        then:"A model is populated containing the domain instance"
            model.webHook == webHook
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/webHook/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def webHook = new WebHook()
            webHook.validate()
            controller.update(webHook)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.webHook == webHook

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            webHook = new WebHook(params).save(flush: true)
            controller.update(webHook)

        then:"A redirect is issued to the show action"
            webHook != null
            response.redirectedUrl == "/webHook/show/$webHook.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/webHook/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def webHook = new WebHook(params).save(flush: true)

        then:"It exists"
            WebHook.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(webHook)

        then:"The instance is deleted"
            WebHook.count() == 0
            response.redirectedUrl == '/webHook/index'
            flash.message != null
    }
}
