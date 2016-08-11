package gki

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.boot.actuate.health.DataSourceHealthIndicator

@EnableScheduling
class Application extends GrailsAutoConfiguration {
  static void main(String[] args) {
    GrailsApp.run(Application, args)
  }

  @Override
  Closure doWithSpring() {
    { ->
      databaseHealthCheck(DataSourceHealthIndicator, dataSource)
     }
  }
}