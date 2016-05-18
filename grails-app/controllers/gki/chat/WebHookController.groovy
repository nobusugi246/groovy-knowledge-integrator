package gki.chat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class WebHookController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond WebHook.list(params), model:[webHookCount: WebHook.count()]
    }

    def show(WebHook webHook) {
        respond webHook
    }

    def create() {
        respond new WebHook(params)
    }

    @Transactional
    def save(WebHook webHook) {
        if (webHook == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (webHook.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond webHook.errors, view:'create'
            return
        }

        webHook.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'webHook.label', default: 'WebHook'), webHook.id])
                redirect webHook
            }
            '*' { respond webHook, [status: CREATED] }
        }
    }

    def edit(WebHook webHook) {
        respond webHook
    }

    @Transactional
    def update(WebHook webHook) {
        if (webHook == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (webHook.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond webHook.errors, view:'edit'
            return
        }

        webHook.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'webHook.label', default: 'WebHook'), webHook.id])
                redirect webHook
            }
            '*'{ respond webHook, [status: OK] }
        }
    }

    @Transactional
    def delete(WebHook webHook) {

        if (webHook == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        webHook.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'webHook.label', default: 'WebHook'), webHook.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'webHook.label', default: 'WebHook'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
