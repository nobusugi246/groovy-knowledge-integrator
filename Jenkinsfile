node {
  stage 'Checkout'
  git url: 'http://localhost:8280/git/nobusugi246/groovy-knowledge-integrator.git'

  stage 'Assemble'
  sh './gradlew clean assemble'

  stage 'Static Code Check'
  sh './gradlew codenarcMain'

  stage 'Test'
  try {
    sh './gradlew --continue test integrationTest jacoco'
  } catch (e) {
    println e
  }
    
  stage 'ResultArchiver'
  step([$class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: 'build/test-results/*.xml'])
  archiveArtifacts 'build/libs/*.jar'
  publishHTML(target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/codenarc', reportFiles: 'main.html', reportName: 'Codenarc Report'])
  publishHTML(target: [allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'build/reports/jacoco/test/html', reportFiles: 'index.html', reportName: 'Jacoco Report'])
}
