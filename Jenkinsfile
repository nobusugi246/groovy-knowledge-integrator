node {
  stage 'Checkout'
  git url: 'http://localhost:8280/git/nobusugi246/groovy-knowledge-integrator.git'

  stage 'Build'
  sh './gradlew clean assemble'

  stage 'Test'
  try {
    sh './gradlew --continue codenarcMain test integrationTest'
  } catch (e) {
    println e
  }
    
  stage 'ResultArchiver'
  step([$class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: 'build/test-results/*.xml'])
  // step([$class: 'JacocoPublisher'])
}
