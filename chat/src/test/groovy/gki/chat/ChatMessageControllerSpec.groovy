package gki.chat

import grails.test.mixin.*
import spock.lang.*

@TestFor(ChatMessageController)
@Mock(ChatMessage)
class ChatMessageControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params["status"] = 'fixed'
        params["chatroom"] = '0'
        params["text"] = 'test message'
        params["username"] = 'testname'
        params["date"] = '2001-01-01'
        params["time"] = '01:02:03'
    }

    void "Test the index action returns the correct model"() {
        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.chatMessageList
            model.chatMessageCount == 0
    }

  /*  
    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.chatMessage!= null
    }

    void "Test the save action correctly persists an instance"() {
        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def chatMessage = new ChatMessage()
            chatMessage.validate()
            controller.save(chatMessage)

        then:"The create view is rendered again with the correct model"
            model.chatMessage!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            chatMessage = new ChatMessage(params)

            controller.save(chatMessage)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/chatMessage/show/1'
            controller.flash.message != null
            ChatMessage.count() == 1
    }
  */

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def chatMessage = new ChatMessage(params)
            controller.show(chatMessage)

        then:"A model is populated containing the domain instance"
            model.chatMessage == chatMessage
    }

  /*
    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def chatMessage = new ChatMessage(params)
            controller.edit(chatMessage)

        then:"A model is populated containing the domain instance"
            model.chatMessage == chatMessage
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/chatMessage/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def chatMessage = new ChatMessage()
            chatMessage.validate()
            controller.update(chatMessage)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.chatMessage == chatMessage

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            chatMessage = new ChatMessage(params).save(flush: true)
            controller.update(chatMessage)

        then:"A redirect is issued to the show action"
            chatMessage != null
            response.redirectedUrl == "/chatMessage/show/$chatMessage.id"
            flash.message != null
    }
  */

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/chatMessage/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def chatMessage = new ChatMessage(params).save(flush: true)

        then:"It exists"
            ChatMessage.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(chatMessage)

        then:"The instance is deleted"
            ChatMessage.count() == 0
            response.redirectedUrl == '/chatMessage/index'
            flash.message != null
    }
}
