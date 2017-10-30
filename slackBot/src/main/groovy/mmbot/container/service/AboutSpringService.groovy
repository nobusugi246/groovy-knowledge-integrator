package mmbot.container.service

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import mmbot.container.dataset.CommandResponse
import mmbot.container.domain.BotRepository
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Lazy
@Slf4j
@Transactional
@CompileStatic
@Service
class AboutSpringService {

    BotRepository botRepository

    AboutSpringService(BotRepository botRepository){
        this.botRepository = botRepository
    }

    CommandResponse about(){
        def cr = new CommandResponse()
        cr.with {
            responseType = 'ephemeral'
            text = """Springに関する情報源です。
| URL | 説明 |
|-----|------|
| https://spring.io | Spring トップページ |
"""
        }

        return cr
    }
}
