node {
  stage('Checkout'){
    git branch: 'tab', url: 'http://localhost:8280/git/nobusugi246/groovy-knowledge-integrator.git'
  }

  dir('chat') {
    stage('Chat Build'){
      sh './gradlew clean build jacoco'
    }
  
    stage('Chat ResultArchiver'){
      archiveArtifacts 'build/libs/*.jar'

      junit allowEmptyResults: true, testResults: 'build/test-results/*.xml'
      step([$class: 'JacocoPublisher'])
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/codenarc', reportFiles: 'main.html', reportName: 'Codenarc Report Of Chat'])
    }
  }

  dir('modeling') {
    stage('Modeling Build'){
      sh './gradlew clean build jacoco'
    }
  
    stage('Modeling ResultArchiver'){
      archiveArtifacts 'build/libs/*.jar'
      
      junit allowEmptyResults: true, testResults: 'build/test-results/*.xml'
      step([$class: 'JacocoPublisher'])
      publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/codenarc', reportFiles: 'main.html', reportName: 'Codenarc Report Of Modeling'])
    }
  }
}
