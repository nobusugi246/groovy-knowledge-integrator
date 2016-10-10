package gki.chat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ChatRoomController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ChatRoom.list(params), model:[chatRoomCount: ChatRoom.count()]
    }

    def show(ChatRoom chatRoom) {
        respond chatRoom
    }

    def create() {
        respond new ChatRoom(params)
    }

    @Transactional
    def save(ChatRoom chatRoom) {
        if (chatRoom == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatRoom.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatRoom.errors, view:'create'
            return
        }

        chatRoom.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'chatRoom.label', default: 'ChatRoom'), chatRoom.id])
                redirect chatRoom
            }
            '*' { respond chatRoom, [status: CREATED] }
        }
    }

    def edit(ChatRoom chatRoom) {
        respond chatRoom
    }

    @Transactional
    def update(ChatRoom chatRoom) {
        if (chatRoom == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatRoom.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatRoom.errors, view:'edit'
            return
        }

        chatRoom.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'chatRoom.label', default: 'ChatRoom'), chatRoom.id])
                redirect chatRoom
            }
            '*'{ respond chatRoom, [status: OK] }
        }
    }

    @Transactional
    def delete(ChatRoom chatRoom) {

        if (chatRoom == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        chatRoom.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'chatRoom.label', default: 'ChatRoom'), chatRoom.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'chatRoom.label', default: 'ChatRoom'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
