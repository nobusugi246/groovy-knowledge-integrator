package gki.container.common

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString
import groovy.util.logging.Slf4j

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
@Immutable
@CompileStatic
@ToString(includeNames=true)
class ChatMessage {
    long id
    String chatroom
    String text
    String username
    String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
    String dmtarget
}
