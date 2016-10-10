package gki.chat

import grails.test.mixin.*
import spock.lang.*

@TestFor(JenkinsController)
@Mock(Jenkins)
class JenkinsControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        params['name'] = 'abc123'
        params['url'] = 'http://def456.com'
        params['username'] = 'ghi789'
        params['password'] = 'jklmno'
        params['created'] = '2010-10-10'
        params['enabled'] = true
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.jenkinsList
            model.jenkinsCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.jenkins!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def jenkins = new Jenkins()
            jenkins.validate()
            controller.save(jenkins)

        then:"The create view is rendered again with the correct model"
            model.jenkins!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            jenkins = new Jenkins(params)

            controller.save(jenkins)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/jenkins/show/1'
            controller.flash.message != null
            Jenkins.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def jenkins = new Jenkins(params)
            controller.show(jenkins)

        then:"A model is populated containing the domain instance"
            model.jenkins == jenkins
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def jenkins = new Jenkins(params)
            controller.edit(jenkins)

        then:"A model is populated containing the domain instance"
            model.jenkins == jenkins
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/jenkins/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def jenkins = new Jenkins()
            jenkins.validate()
            controller.update(jenkins)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.jenkins == jenkins

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            jenkins = new Jenkins(params).save(flush: true)
            controller.update(jenkins)

        then:"A redirect is issued to the show action"
            jenkins != null
            response.redirectedUrl == "/jenkins/show/$jenkins.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/jenkins/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def jenkins = new Jenkins(params).save(flush: true)

        then:"It exists"
            Jenkins.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(jenkins)

        then:"The instance is deleted"
            Jenkins.count() == 0
            response.redirectedUrl == '/jenkins/index'
            flash.message != null
    }
}
