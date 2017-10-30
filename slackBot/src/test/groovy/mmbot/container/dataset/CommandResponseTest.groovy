package mmbot.container.dataset

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.context.junit4.SpringRunner

import static org.assertj.core.api.Assertions.*

@RunWith(SpringRunner)
@JsonTest
class CommandResponseTest {
    @Autowired
    private JacksonTester<CommandResponse> json

    @Test
    void commandResponseToJsonTest(){
        def response = new CommandResponse(text: '日本語のテスト')
        def converted = this.json.write(response)

        assertThat(converted).hasJsonPathStringValue('@.response_type')
        assertThat(converted).hasJsonPathStringValue('@.username')
        assertThat(converted).hasJsonPathStringValue('@.icon_url')

        assertThat(converted)
                .extractingJsonPathStringValue('@.text')
                .isEqualTo('日本語のテスト')
    }
}
