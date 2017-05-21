package gki.container.dataset

import gki.container.domain.ChatServer
import groovy.transform.CompileStatic
import groovy.transform.ToString

@CompileStatic
@ToString(includeNames=true)
class ExecDataSet {
    Iterable<ChatServer> chatServers
    ChatMessage message
}
