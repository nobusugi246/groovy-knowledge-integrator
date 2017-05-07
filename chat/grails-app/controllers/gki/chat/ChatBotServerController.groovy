package gki.chat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ChatBotServerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ChatBotServer.list(params), model:[chatBotServerCount: ChatBotServer.count()]
    }

    def show(ChatBotServer chatBotServer) {
        respond chatBotServer
    }

    def create() {
        respond new ChatBotServer(params)
    }

    @Transactional
    def save(ChatBotServer chatBotServer) {
        if (chatBotServer == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatBotServer.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatBotServer.errors, view:'create'
            return
        }

        chatBotServer.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'chatBotServer.label', default: 'ChatBotServer'), chatBotServer.id])
                redirect chatBotServer
            }
            '*' { respond chatBotServer, [status: CREATED] }
        }
    }

    def edit(ChatBotServer chatBotServer) {
        respond chatBotServer
    }

    @Transactional
    def update(ChatBotServer chatBotServer) {
        if (chatBotServer == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (chatBotServer.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond chatBotServer.errors, view:'edit'
            return
        }

        chatBotServer.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'chatBotServer.label', default: 'ChatBotServer'), chatBotServer.id])
                redirect chatBotServer
            }
            '*'{ respond chatBotServer, [status: OK] }
        }
    }

    @Transactional
    def delete(ChatBotServer chatBotServer) {

        if (chatBotServer == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        chatBotServer.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'chatBotServer.label', default: 'ChatBotServer'), chatBotServer.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'chatBotServer.label', default: 'ChatBotServer'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
