package gki.container.common

import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames=true)
class ChatMessage {
    long id
    String chatroom
    String text
    String username
    String date
    String time
}
