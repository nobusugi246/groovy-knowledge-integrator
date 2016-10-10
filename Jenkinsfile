node {
  stage('Checkout'){
    git url: 'http://localhost:8280/git/nobusugi246/groovy-knowledge-integrator.git'
  }

  parallel chat: {
    dir('chat'){
      stage('Assemble'){
        sh './gradlew clean assemble'
      }
  
      stage('Static Code Check'){
        sh './gradlew codenarcMain'
      }
  
      stage('Test'){
        try {
          sh './gradlew --continue test integrationTest jacoco'
        } catch (e) {
          println e
        }
      }
    
      stage('ResultArchiver'){
        archiveArtifacts 'build/libs/*.jar'

        junit allowEmptyResults: true, testResults: 'build/test-results/*.xml'
        step([$class: 'JacocoPublisher'])
        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/codenarc', reportFiles: 'main.html', reportName: 'Codenarc Report'])
      }
    }
  }, modeling: {
    dir('modeling'){
      stage('Assemble'){
        sh './gradlew clean assemble'
      }
  
      stage('Static Code Check'){
        sh './gradlew codenarcMain'
      }
  
      stage('Test'){
        try {
          sh './gradlew --continue test integrationTest jacoco'
        } catch (e) {
          println e
        }
      }
    
      stage('ResultArchiver'){
        archiveArtifacts 'build/libs/*.jar'

        //        junit allowEmptyResults: true, testResults: 'build/test-results/*.xml'
        //        step([$class: 'JacocoPublisher'])
        //        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/codenarc', reportFiles: 'main.html', reportName: 'Codenarc Report'])
      }
    }
  }, failFast: false
}
