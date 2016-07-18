node {
  stage 'Checkout'
  git url: 'http://localhost:8280/git/nobusugi246/groovy-knowledge-integrator.git'

  stage 'Assemble'
  sh './gradlew clean assemble'

  stage 'Static Code Check'
  sh './gradlew codenarcMain'

  stage 'Test'
  try {
    sh './gradlew --continue test integrationTest'
  } catch (e) {
    println e
  }
    
  stage 'ResultArchiver'
  step([$class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: 'build/test-results/*.xml'])
  // step([$class: 'JacocoPublisher'])
}
