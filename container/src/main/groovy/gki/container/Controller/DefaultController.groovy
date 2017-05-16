package gki.container.controller

import gki.container.common.ChatMessage
import gki.container.common.TestDataSet
import gki.container.service.BotService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Slf4j
@CompileStatic
@RestController
class DefaultController {
    @Autowired
    BotService botService

    @PostMapping('/create')
    String createBot(@RequestParam(name = 'name')String name,
                     @RequestParam(name = 'from')String from,
                     @RequestParam(name = 'user')String userName){

        return botService.createBot(name, from, userName)
    }

    @PostMapping('/chatMessage')
    void receiveChatMessage(@RequestBody ChatMessage message){
        botService.execBotScript(message)
    }

    @PostMapping('/testBot')
    String testBot(@RequestBody TestDataSet dataSet){
        return botService.testBot(dataSet)
    }
}
