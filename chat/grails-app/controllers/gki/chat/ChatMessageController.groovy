package gki.chat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ChatMessageController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ChatMessage.list(params), model:[chatMessageCount: ChatMessage.count()]
    }

    def show(ChatMessage chatMessage) {
        respond chatMessage
    }

    def create() {
        respond new ChatMessage(params)
    }

    @Transactional
    def save(ChatMessage chatMessage) {
        if (chatMessage == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatMessage.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatMessage.errors, view:'create'
            return
        }

        chatMessage.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'chatMessage.label', default: 'ChatMessage'), chatMessage.id])
                redirect chatMessage
            }
            '*' { respond chatMessage, [status: CREATED] }
        }
    }

    def edit(ChatMessage chatMessage) {
        respond chatMessage
    }

    @Transactional
    def update(ChatMessage chatMessage) {
        if (chatMessage == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatMessage.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatMessage.errors, view:'edit'
            return
        }

        chatMessage.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'chatMessage.label', default: 'ChatMessage'), chatMessage.id])
                redirect chatMessage
            }
            '*'{ respond chatMessage, [status: OK] }
        }
    }

    @Transactional
    def delete(ChatMessage chatMessage) {

        if (chatMessage == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        chatMessage.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'chatMessage.label', default: 'ChatMessage'), chatMessage.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'chatMessage.label', default: 'ChatMessage'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
