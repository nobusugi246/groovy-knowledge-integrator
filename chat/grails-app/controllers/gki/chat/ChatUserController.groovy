package gki.chat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ChatUserController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ChatUser.list(params), model:[chatUserCount: ChatUser.count()]
    }

    def show(ChatUser chatUser) {
        respond chatUser
    }

    def create() {
        respond new ChatUser(params)
    }

    @Transactional
    def save(ChatUser chatUser) {
        if (chatUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatUser.errors, view:'create'
            return
        }

        chatUser.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'chatUser.label', default: 'ChatUser'), chatUser.id])
                redirect chatUser
            }
            '*' { respond chatUser, [status: CREATED] }
        }
    }

    def edit(ChatUser chatUser) {
        respond chatUser
    }

    @Transactional
    def update(ChatUser chatUser) {
        if (chatUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatUser.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatUser.errors, view:'edit'
            return
        }

        chatUser.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'chatUser.label', default: 'ChatUser'), chatUser.id])
                redirect chatUser
            }
            '*'{ respond chatUser, [status: OK] }
        }
    }

    @Transactional
    def delete(ChatUser chatUser) {

        if (chatUser == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        chatUser.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'chatUser.label', default: 'ChatUser'), chatUser.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'chatUser.label', default: 'ChatUser'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
