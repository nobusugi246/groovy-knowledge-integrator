package mmbot.container

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.AsyncConfigurerSupport
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

import java.util.concurrent.Executor

@Slf4j
@CompileStatic
@EnableAsync
@SpringBootApplication
@ComponentScan
class Application extends AsyncConfigurerSupport{
    static void main(String[] args) {
        SpringApplication.run(Application, args)
    }


    @Override
    Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor()
        executor.setCorePoolSize(4)
        executor.setMaxPoolSize(4)
        executor.setQueueCapacity(100)
        executor.setThreadNamePrefix("mmbot-")
        executor.initialize()
        return executor
    }
}

