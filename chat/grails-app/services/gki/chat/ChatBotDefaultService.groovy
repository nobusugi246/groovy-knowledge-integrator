package gki.chat

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import groovy.xml.XmlUtil
import org.apache.commons.codec.binary.Base64
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.client.RestTemplate

@Slf4j
@Transactional
class ChatBotDefaultService {

  def infoEndpoint
  def healthEndpoint
  def metricsEndpoint
  
  def chatService
  SimpMessagingTemplate brokerMessagingTemplate

  def commandList = [
    ['hello / help / usage', '利用方法の説明(このメッセージ)', /(こんにちは|今日は|hello|help|usage)/,
     { message -> hello(message.username) }],
    ['makeChatRoom <ChatRoom名>', 'ChatRoomの作成', /(makeChatRoom .+|mcr .+)/,
     { message -> makeChatRoom(message) }],
    ['deleteChatRoom <ChatRoom名>', 'ChatRoomの削除', /(deleteChatRoom .+|dcr .+)/,
     { message -> deleteChatRoom(message) }],
    ['users', '接続している全ユーザと、有効な WebHook, FeedCrawler, Jenkins Jobのリストを表示', /users/,
     { message -> displayAllConnectedUsers(message) }],
    ['addHook <WebHook名> <URL> [<Char Room>]', 'WebHookを登録する', /addHook.*/,
     { message -> addHook(message) }],
    ['deleteHook <WebHook名>', 'WebHookを削除する', /deleteHook.*/,
     { message -> deleteHook(message) }],
    ['addFeed <Feed名> <URL> [<Char Room> <Interval>]', 'Feedを登録する', /addFeed.*/,
     { message -> addFeed(message) }],
    ['deleteFeed <Feed名>', 'Feedを削除する', /deleteFeed.*/,
     { message -> deleteFeed(message) }],
    ['info', 'Spring Boot Actuatorの infoを表示', /info.*/,
     { message -> actuator(message) }],
    ['health', 'Spring Boot Actuatorの healthを表示', /health.*/,
     { message -> actuator(message) }],
    ['metrics [<Group>]', 'Spring Boot Actuatorの metricsを表示', /metrics.*/,
     { message -> actuator(message) }],
    ['addJenkins <Jenkins Job名> <URL> [<Username> <Password>]', 'Jenkins Jobを登録する', /addJenkins.*/,
     { message -> addJenkins(message) }],
    ['deleteJenkins <Jenkins Job名>', 'Jenkins Jobを削除する', /deleteJenkins.*/,
     { message -> deleteJenkins(message) }],
    ['build <Jenkins Job名>', 'Jenkins Jobのビルドを依頼する', /build.*/,
     { message -> buildByJenkins(message) }],
    ['addBotServer <Botコンテナサーバ名> <BotコンテナサーバURL>', 'Botコンテナサーバを登録する', /addBotServer.*/,
     { message -> addBotServer(message) }]
  ]

  
  void defaultHandler(ChatMessage message) {
    commandList.each { commandName, desc, trigger, closure ->
      if( message.text ==~ trigger ) {
        closure.call(message)
      }
    }
  }


  void hello(String username){
    replyMessage username,
                 "こんにちは、${username}さん"
    Thread.sleep(20)

    def messageList = ChatMessage.findAllByUsername(username, [sort: 'id'])
    if( messageList.size >= 2 ){
      def lastMessage = messageList[-2]
            
      replyMessage username,
                   "あなたの最新のメッセージは"
      Thread.sleep(20)
      replyMessage username,
                   "${lastMessage.date} ${lastMessage.time} '${lastMessage.text}'"
      Thread.sleep(20)
      replyMessage username,
                   "です。"
      Thread.sleep(20)
    }

    replyMessage username,
                 "左の <span class='glyphicon glyphicon-user'></span> をクリックすることで、ユーザアイコンのイメージファイルをアップロードできます。"

    Thread.sleep(20)
    replyMessage username,
                 "<span class='glyphicon glyphicon-pencil'></span> チャットメッセージを入力するテキストフィールドをクリックすることで、入力途中表示の有無を切り替えることができます。"

    Thread.sleep(20)
    replyMessage username,
                 "カレンダーには、その日にやり取りしたメッセージ数が表示され、日にちをクリックすることでそのメッセージを見ることができます。"

    Thread.sleep(20)
    replyMessage username,
                 "利用するチャットルームは、「Chat Room」のリストから選択してください。"

    Thread.sleep(20)
    replyMessage username,
                 "チャットルームに入る前に送信されたメッセージは、「Past」内に表示されます。"

    Thread.sleep(20)
    replyMessage username,
                 "受信したメッセージをクリックすることで、引用できます。"

    Thread.sleep(20)
    replyMessage username,
                 "Ctrl + i により、直前に自分で送信したメッセージを、入力フィールドに再入力できます。 "

    Thread.sleep(20)
    replyMessage username,
                 "このチャットシステムで利用できるコマンドは以下です。"

    Thread.sleep(20)
    commandList.each { commandName, desc, trigger, closure ->
      replyMessage username, "&nbsp; &nbsp; ${XmlUtil.escapeXml commandName}: ${XmlUtil.escapeXml desc}"
      Thread.sleep(20)
    }    
  }


  void makeChatRoom(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()
    if ( words.size() != 2) {
      replyMessage message.chatroom,
                   "${message.username}さん, ChatRoomの指定が正しくありません。"
    } else {
      if (ChatRoom.findByName(words[1])) {
        replyMessage message.chatroom,
                     "${message.username}さん, ChatRoom '${words[1]}'は既にあります。"
      } else {
        new ChatRoom(name: words[1]).save()
        replyMessage message.chatroom,
                     "${message.username}さん, ChatRoom '${words[1]}'を作成しました。",
                     true
        chatService.sendUserList()
      }
    }
  }
  
  
  void deleteChatRoom(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()
    if ( words.size() != 2) {
      replyMessage message.chatroom,
                   "${message.username}さん, ChatRoomの指定が正しくありません。"
    } else {
      def target = ChatRoom.findByName(words[1])
      if (target) {
        target.delete()
        replyMessage message.chatroom,
                     "${message.username}さん, ChatRoom '${words[1]}'を削除しました。",
                     true
        chatService.sendUserList()
      } else {
        replyMessage message.chatroom,
                     "${message.username}さん, ChatRoom '${words[1]}'は有りません。"
      }
    }
  }


  void displayAllConnectedUsers(ChatMessage message) {
    def userList = ChatUser.findAllWhere(enabled: true)
    def whList = WebHook.findAllWhere(enabled: true)
    def fcList = FeedCrawler.findAllWhere(enabled: true)
    def jsList = Jenkins.findAllWhere(enabled: true)

    replyMessage message.username,
                 "${message.username}さん, 接続中のユーザは ${userList.size}名, 有効な WebHookは ${whList.size}, FeedCrawlerは ${fcList.size}, Jenkins Jobは ${jsList.size} です。"

    userList.each { user ->
      def chatroom = ChatRoom.get(user.chatroom)
      replyMessage message.username,
                   "${user.username}さんは '${chatroom.name}' にいます。"
    }

    whList.each { wh ->
      replyMessage message.username,
                   "WebHook '${wh.hookName}' (${wh.hookFrom}) が有効です。"
    }

    fcList.each { crawler ->
      replyMessage message.username,
                   "Feed '${crawler.name}' (${crawler.url}) が有効です。"
    }

    jsList.each { jenkins ->
      replyMessage message.username,
                   "Jenkins Job '${jenkins.name}' (${jenkins.url}) が有効です。"
    }
  }

  
  void addHook(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() <= 2 ) {
      replyMessage message.username,
                   XmlUtil.escapeXml("addHook <WebHook名> <URL> [<Char Room>] と入力してください。")
      return
    }

    if( WebHook.findByHookName(words[1]) ) {
      replyMessage message.username,
              "すでに '${words[1]}' という WebHook は登録されています。"
      return
    }

    if( words.size() == 3 ){
      words << ChatRoom.get(message.chatroom as long).name
    }

    new WebHook(hookName: words[1], hookFrom: words[2], chatroom: words[3]).save()

    if( WebHook.findByHookName(words[1]) ){
      replyMessage message.chatroom,
                   XmlUtil.escapeXml("WebHook '${words[1]}' を登録しました。"),
                   true
    }
  }


  void deleteHook(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() != 2 ) {
      replyMessage message.username,
                   XmlUtil.escapeXml("deleteHook <WebHook名> と入力してください。")
      return
    }

    WebHook.findByHookName(words[1]).delete()

    if( !WebHook.findByHookName(words[1]) ){
      replyMessage message.chatroom,
                   XmlUtil.escapeXml("WebHook '${words[1]}' を削除しました。"),
                   true
    }
  }


  void addFeed(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() <= 2 ) {
      replyMessage message.username,
              XmlUtil.escapeXml("addFeed <Feed名> <URL> [<Char Room> <Interval>] と入力してください。")
      return
    }

    if( FeedCrawler.findByName(words[1]) ) {
      replyMessage message.username,
              "すでに '${words[1]}' という Feed は登録されています。"
      return
    }

    if( words.size() == 3 ){
      words << ChatRoom.get(message.chatroom as long).name
    }

    if( words.size() == 4 ){
      words << 30
    }

    new FeedCrawler(name: words[1], url: words[2], chatroom: words[3], interval: words[4]).save()

    if( FeedCrawler.findByName(words[1]) ){
      replyMessage message.chatroom,
                   XmlUtil.escapeXml("Feed '${words[1]}' を登録しました。"),
                   true
    }
  }


  void deleteFeed(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() != 2 ) {
      replyMessage message.username,
              XmlUtil.escapeXml("deleteFeed <Feed名> と入力してください。")
      return
    }

    FeedCrawler.findByName(words[1]).delete()

    if( !FeedCrawler.findByName(words[1]) ){
      replyMessage message.chatroom,
                   XmlUtil.escapeXml("Feed '${words[1]}' を削除しました。"),
                   true
    }
  }


  void actuator(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()
    def type = words[0]
    def group = ''
    if( words.size() > 1){
      group = words[1]
    }
    def endpoints = ['info': infoEndpoint, 'health': healthEndpoint,
                     'metrics': metricsEndpoint
                    ]
    def endpoint = endpoints[type]

    replyMessage message.username,
                 "${message.username}さん, このチャットサーバの ${type}です。"

    def result
    if( type == 'health' ) {
      def health = endpoint.invoke()
      def status = health.getStatus()

      replyMessage message.username, "status : ${status}"
      result = health.getDetails()
    } else {
      result = endpoint.invoke()
    }

    result.each { key, value ->
      if(group) {
        if (key =~ /^${group}/) {
          replyMessage message.username, "${key} : ${value}"
        }
      } else {
          replyMessage message.username, "${key} : ${value}"
      }
      Thread.sleep(20)
    }
  }

  
  void addJenkins(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() != 3 && words.size() != 5 ) {
      replyMessage message.username,
                   XmlUtil.escapeXml("addJenkins <Jenkins Job名> <URL> [<Username> <Password>] と入力してください。")
      return
    }

    if( Jenkins.findByName(words[1]) ) {
      replyMessage message.username,
              "すでに '${words[1]}' という Jenkins Jobは登録されています。"
      return
    }

    if( words.size() == 3 ) {
      words << '' << ''
    }

    new Jenkins(name: words[1], url: words[2], username: words[3], password: words[4]).save()

    if( Jenkins.findByName(words[1]) ){
      replyMessage message.chatroom,
                   XmlUtil.escapeXml("Jenkins Job '${words[1]}' を登録しました。"),
                   true
    }
  }


  void deleteJenkins(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() != 2 ) {
      replyMessage message.username,
                   XmlUtil.escapeXml("deleteJenkins <Jenkins Job名> と入力してください。")
      return
    }

    Jenkins.findByName(words[1]).delete()

    if( !Jenkins.findByName(words[1]) ){
      replyMessage message.chatroom,
                   XmlUtil.escapeXml("Jenkins Job '${words[1]}' を削除しました。"),
                   true
    }
  }


  void buildByJenkins(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() != 2 ) {
      replyMessage message.username,
              XmlUtil.escapeXml("build <Jenkins Job名> と入力してください。")
      return
    }

    def target = Jenkins.findByName(words[1])

    if( !target ) {
      replyMessage message.username,
              XmlUtil.escapeXml("Jenkins Job '${words[1]}' は有りません。")
      return
    }

    def jenkins = new RestTemplate()
    String url = "${target.url}/build"

    String plainCreds = "${target.username}:${target.password}"
    byte[] plainCredsBytes = plainCreds.getBytes()
    byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes)
    String base64Creds = new String(base64CredsBytes)
    
    def headers = new HttpHeaders()
    headers.add('Authorization', "Basic ${base64Creds}")
    def request = new HttpEntity<String>(headers)

    try {
      jenkins.exchange(url, HttpMethod.POST, request, String)
      //      jenkins.postForObject(url, request, String)
    } catch (e) {
      log.error "Exception: ${e.message}"
      replyMessage message.username,
                   XmlUtil.escapeXml("Jenkins Job '${words[1]}' のビルドの依頼に失敗しました: ${e.message}")
      return
    }

    replyMessage message.chatroom,
                 XmlUtil.escapeXml("Jenkins Job '${words[1]}' のビルドを依頼しました。"),
                 true
  }
  

  void webhook(payload) {
    def url = payload.repository.html_url

    log.info url
    def wh = WebHook.findByHookFrom(url)
    if( !wh || !wh.enabled ) return
    
    def roomList = wh.chatroom

    if( roomList ) {
      roomList = ChatRoom.findByName roomList
    } else {
      roomList = ChatRoom.findAll()
    }

    roomList.each { room ->
      def to = room.id as String

      if( payload.pusher ) {
        replyMessage to, "レポジトリに Pushされました。", true, wh.hookName
      } else if( payload.issue ) {
        if( payload.action == 'opened' ) {
          replyMessage to, "レポジトリに Issueが作成されました。", true, wh.hookName
        } else if( payload.action == 'closed' ) {
          replyMessage to, "レポジトリの Issueがクローズされました。", true, wh.hookName
        } else if( payload.action == 'reopened' ) {
          replyMessage to, "レポジトリの Issueが再開されました。", true, wh.hookName
        } else if( payload.action == 'created' && payload.comment ) {
          replyMessage to, "レポジトリの Issueにコメントが追加されました。", true, wh.hookName
        }
      } else if( payload.pull_request ) {
        if( payload.action == 'opened' ) {
          replyMessage to, "レポジトリに Pull Requestが作成されました。", true, wh.hookName
        } else if( payload.action == 'closed' ) {
          replyMessage to, "レポジトリの Pull Requestがクローズされました。", true, wh.hookName
        } else if( payload.action == 'reopened' ) {
          replyMessage to, "レポジトリの Pull Requestが再開されました。", true, wh.hookName
        }
      }

      replyMessage to, "repository: <a href='${url}' target='_blank'>${url}</a>", true, wh.hookName

      if( payload.pusher ) {
        replyMessage to, "ref: ${payload.ref}", true, wh.hookName
      }

      if( payload.issue ) {
        def issueurl = "${url}/issues/${payload.issue.number}"
        replyMessage to, "issue: <a href='${issueurl}' target='_blank'>No. ${payload.issue.number}</a>", true, wh.hookName
        replyMessage to, "title: ${payload.issue.title}", true, wh.hookName
      }

      if( payload.pull_request ) {
        def prurl = "${url}/pull/${payload.pull_request.number}"
        replyMessage to, "pull request: <a href='${prurl}' target='_blank'>No. ${payload.pull_request.number}</a>", true, wh.hookName
        replyMessage to, "title: ${payload.pull_request.title}", true, wh.hookName
      }

      if( payload.comment ) {
        replyMessage to, "comment: ${payload.comment.body}", true, wh.hookName
      }

      if( payload.sender ) {
        replyMessage to, "by: ${payload.sender.login}", true, wh.hookName
      }

      replyMessage to, "<hr/>", true, wh.hookName
    }
  }

  
  void addBotServer(ChatMessage message) {
    def words = message.text.split(/[ \t]+/).toList()

    if( words.size() != 3 ) {
      replyMessage message.username,
                   XmlUtil.escapeXml("addBotServer <Botコンテナサーバ名> <BotコンテナサーバURL>と入力してください。")
      return
    }

    if( ChatBotServer.findByName(words[1]) ) {
      replyMessage message.username,
              "すでに '${words[1]}' という Botコンテナサーバ は登録されています。"
      return
    }

    new ChatBotServer(name: words[1], uri: words[2]).save()

    if( ChatBotServer.findByName(words[1]) ){
      replyMessage message.chatroom,
                   XmlUtil.escapeXml("Botコンテナサーバ '${words[1]}' を登録しました。"),
                   true
    }
  }


  void replyMessage(String to, String message,
                    boolean persistence = false,
                    String replyname = 'gkibot') {

    String replyto = "/topic/${to}"
    def msg = new ChatMessage(text: message, username: replyname)

    if (persistence) {
      msg.status = 'fixed'
      msg.chatroom = to
      msg.save()
    }

    brokerMessagingTemplate.convertAndSend replyto, (msg as JSON).toString()
  }
}
