package gki.chat

import grails.test.mixin.*
import spock.lang.*

@TestFor(ChatBotServerController)
@Mock(ChatBotServer)
class ChatBotServerControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params["name"] = 'default'
        params["uri"] = 'http://localhost:8081'
        params["enabled"] = true
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.chatBotServerList
            model.chatBotServerCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.chatBotServer!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def chatBotServer = new ChatBotServer()
            chatBotServer.validate()
            controller.save(chatBotServer)

        then:"The create view is rendered again with the correct model"
            model.chatBotServer!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            chatBotServer = new ChatBotServer(params)

            controller.save(chatBotServer)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/chatBotServer/show/1'
            controller.flash.message != null
            ChatBotServer.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def chatBotServer = new ChatBotServer(params)
            controller.show(chatBotServer)

        then:"A model is populated containing the domain instance"
            model.chatBotServer == chatBotServer
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def chatBotServer = new ChatBotServer(params)
            controller.edit(chatBotServer)

        then:"A model is populated containing the domain instance"
            model.chatBotServer == chatBotServer
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/chatBotServer/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def chatBotServer = new ChatBotServer()
            chatBotServer.validate()
            controller.update(chatBotServer)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.chatBotServer == chatBotServer

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            chatBotServer = new ChatBotServer(params).save(flush: true)
            controller.update(chatBotServer)

        then:"A redirect is issued to the show action"
            chatBotServer != null
            response.redirectedUrl == "/chatBotServer/show/$chatBotServer.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/chatBotServer/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def chatBotServer = new ChatBotServer(params).save(flush: true)

        then:"It exists"
            ChatBotServer.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(chatBotServer)

        then:"The instance is deleted"
            ChatBotServer.count() == 0
            response.redirectedUrl == '/chatBotServer/index'
            flash.message != null
    }
}
