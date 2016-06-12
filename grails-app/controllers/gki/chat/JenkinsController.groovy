package gki.chat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class JenkinsController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Jenkins.list(params), model:[jenkinsCount: Jenkins.count()]
    }

    def show(Jenkins jenkins) {
        respond jenkins
    }

    def create() {
        respond new Jenkins(params)
    }

    @Transactional
    def save(Jenkins jenkins) {
        if (jenkins == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (jenkins.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond jenkins.errors, view:'create'
            return
        }

        jenkins.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'jenkins.label', default: 'Jenkins'), jenkins.id])
                redirect jenkins
            }
            '*' { respond jenkins, [status: CREATED] }
        }
    }

    def edit(Jenkins jenkins) {
        respond jenkins
    }

    @Transactional
    def update(Jenkins jenkins) {
        if (jenkins == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (jenkins.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond jenkins.errors, view:'edit'
            return
        }

        jenkins.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'jenkins.label', default: 'Jenkins'), jenkins.id])
                redirect jenkins
            }
            '*'{ respond jenkins, [status: OK] }
        }
    }

    @Transactional
    def delete(Jenkins jenkins) {

        if (jenkins == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        jenkins.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'jenkins.label', default: 'Jenkins'), jenkins.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'jenkins.label', default: 'Jenkins'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
