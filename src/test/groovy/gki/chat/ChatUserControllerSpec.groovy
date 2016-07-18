package gki.chat

import grails.test.mixin.*
import spock.lang.*

@TestFor(ChatUserController)
@Mock(ChatUser)
class ChatUserControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params["username"] = 'abc123'
        params["password"] = 'def456'
        params["role"] = 'User'
        params["created"] = '2001-01-01'
        params["enabled"] = false
        params["chatroom"] = 0
        params["iconImage"] = null
        params["heartbeatCount"] = 0
    }

    void "Test the index action returns the correct model"() {
        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.chatUserList
            model.chatUserCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.chatUser!= null
    }

    void "Test the save action correctly persists an instance"() {
        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def chatUser = new ChatUser()
            chatUser.validate()
            controller.save(chatUser)

        then:"The create view is rendered again with the correct model"
            model.chatUser!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            chatUser = new ChatUser(params)

            controller.save(chatUser)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/chatUser/show/1'
            controller.flash.message != null
            ChatUser.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def chatUser = new ChatUser(params)
            controller.show(chatUser)

        then:"A model is populated containing the domain instance"
            model.chatUser == chatUser
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def chatUser = new ChatUser(params)
            controller.edit(chatUser)

        then:"A model is populated containing the domain instance"
            model.chatUser == chatUser
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/chatUser/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def chatUser = new ChatUser()
            chatUser.validate()
            controller.update(chatUser)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.chatUser == chatUser

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            chatUser = new ChatUser(params).save(flush: true)
            controller.update(chatUser)

        then:"A redirect is issued to the show action"
            chatUser != null
            response.redirectedUrl == "/chatUser/show/$chatUser.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/chatUser/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def chatUser = new ChatUser(params).save(flush: true)

        then:"It exists"
            ChatUser.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(chatUser)

        then:"The instance is deleted"
            ChatUser.count() == 0
            response.redirectedUrl == '/chatUser/index'
            flash.message != null
    }
}
