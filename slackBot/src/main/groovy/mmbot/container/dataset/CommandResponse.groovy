package mmbot.container.dataset

import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.springframework.stereotype.Component

@Component
@CompileStatic
@ToString(includeNames=true)
class CommandResponse {
    String responseType = ''
    String text = ''
    String username = ''
    String iconUrl = ''
}
