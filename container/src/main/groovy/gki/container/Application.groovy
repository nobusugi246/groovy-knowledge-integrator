package gki.container

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@Slf4j
@CompileStatic
@SpringBootApplication
@ComponentScan(['gki.container','asset.pipeline.springboot'])
class Application {
    static void main(String[] args) {
        SpringApplication.run(Application, args)
    }
}

