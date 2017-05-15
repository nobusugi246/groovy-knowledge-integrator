node {
  stage('Checkout'){
    git branch: 'master', url: 'http://localhost:8280/git/nobusugi246/groovy-knowledge-integrator.git'
  }

  dir('chat') {
    stage('Chat Build'){
      try {
        sh './gradlew clean build jacocoTR'
      } catch (e) {
        println e.message
      }
    }
  
    stage('Chat ResultArchiver'){
      archiveArtifacts 'build/libs/*.jar'

      junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
      step([$class: 'JacocoPublisher'])
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/codenarc', reportFiles: 'main.html', reportName: 'Codenarc Report Of Chat'])
    }
  }

  dir('container') {
    stage('Bot Container Build'){
      try {
        sh './gradlew clean build jacocoTR'
      } catch (e) {
        println e.message
      }
    }
  
    stage('Bot Container ResultArchiver'){
      archiveArtifacts 'build/libs/*.jar'
      
      junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
      step([$class: 'JacocoPublisher'])
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/codenarc', reportFiles: 'main.html', reportName: 'Codenarc Report Of Modeling'])
    }
  }
}
