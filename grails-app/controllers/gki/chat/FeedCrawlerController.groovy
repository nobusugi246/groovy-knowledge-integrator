package gki.chat

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class FeedCrawlerController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond FeedCrawler.list(params), model:[feedCrawlerCount: FeedCrawler.count()]
    }

    def show(FeedCrawler feedCrawler) {
        respond feedCrawler
    }

    def create() {
        respond new FeedCrawler(params)
    }

    @Transactional
    def save(FeedCrawler feedCrawler) {
        if (feedCrawler == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (feedCrawler.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond feedCrawler.errors, view:'create'
            return
        }

        feedCrawler.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'feedCrawler.label', default: 'FeedCrawler'), feedCrawler.id])
                redirect feedCrawler
            }
            '*' { respond feedCrawler, [status: CREATED] }
        }
    }

    def edit(FeedCrawler feedCrawler) {
        respond feedCrawler
    }

    @Transactional
    def update(FeedCrawler feedCrawler) {
        if (feedCrawler == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (feedCrawler.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond feedCrawler.errors, view:'edit'
            return
        }

        feedCrawler.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'feedCrawler.label', default: 'FeedCrawler'), feedCrawler.id])
                redirect feedCrawler
            }
            '*'{ respond feedCrawler, [status: OK] }
        }
    }

    @Transactional
    def delete(FeedCrawler feedCrawler) {

        if (feedCrawler == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        feedCrawler.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'feedCrawler.label', default: 'FeedCrawler'), feedCrawler.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'feedCrawler.label', default: 'FeedCrawler'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
