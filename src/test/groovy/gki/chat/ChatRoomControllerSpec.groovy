package gki.chat

import grails.test.mixin.*
import spock.lang.*

@TestFor(ChatRoomController)
@Mock(ChatRoom)
class ChatRoomControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params['name'] = 'abc123'
        params['created'] = '2016-06-10'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.chatRoomList
            model.chatRoomCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.chatRoom!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def chatRoom = new ChatRoom()
            chatRoom.validate()
            controller.save(chatRoom)

        then:"The create view is rendered again with the correct model"
            model.chatRoom == null
            view != 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            chatRoom = new ChatRoom(params)

            controller.save(chatRoom)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/chatRoom/show/2'
            controller.flash.message != null
            ChatRoom.count() == 2
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def chatRoom = new ChatRoom(params)
            controller.show(chatRoom)

        then:"A model is populated containing the domain instance"
            model.chatRoom == chatRoom
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def chatRoom = new ChatRoom(params)
            controller.edit(chatRoom)

        then:"A model is populated containing the domain instance"
            model.chatRoom == chatRoom
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/chatRoom/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def chatRoom = new ChatRoom()
            chatRoom.validate()
            controller.update(chatRoom)

        then:"The edit view is rendered again with the invalid instance"
            view != 'edit'
            // model.chatRoom == chatRoom
            model.chatRoom == null

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            chatRoom = new ChatRoom(params).save(flush: true)
            controller.update(chatRoom)

        then:"A redirect is issued to the show action"
            chatRoom != null
            response.redirectedUrl == "/chatRoom/show/$chatRoom.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/chatRoom/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def chatRoom = new ChatRoom(params).save(flush: true)

        then:"It exists"
            ChatRoom.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(chatRoom)

        then:"The instance is deleted"
            ChatRoom.count() == 0
            response.redirectedUrl == '/chatRoom/index'
            flash.message != null
    }
}
