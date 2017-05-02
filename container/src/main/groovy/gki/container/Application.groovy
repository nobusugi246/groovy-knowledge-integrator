package gki.container

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@CompileStatic
@SpringBootApplication
@ComponentScan(['gki.container','asset.pipeline.springboot'])
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application, args)
    }
}
