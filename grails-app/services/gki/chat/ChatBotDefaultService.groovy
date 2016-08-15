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

  ChatMessage message
  
  def commandList = [
    ['hello', '利用方法の説明(このメッセージ)', /(こんにちは|今日は|hello|help|usage)/,
     { hello(this.message.username) }],
    ['makeChatRoom <ChatRoom名>', 'ChatRoomの作成', /(makeChatRoom .+|mcr .+)/,
     { makeChatRoom(this.message) }],
    ['deleteChatRoom <ChatRoom名>', 'ChatRoomの削除', /(deleteChatRoom .+|dcr .+)/,
     { deleteChatRoom(this.message) }],
    ['users', '接続している全ユーザと、有効な WebHook, FeedCrawler, Jenkins Jobのリストを表示', /users/,
     { displayAllConnectedUsers() }],
    ['addHook <WebHook名> <URL> [<Char Room>]', 'WebHookを登録する', /addHook.*/,
     { addHook(this.message) }],
    ['deleteHook <WebHook名>', 'WebHookを削除する', /deleteHook.*/,
     { deleteHook(this.message) }],
    ['addFeed <Feed名> <URL> [<Char Room> <Interval>]', 'Feedを登録する', /addFeed.*/,
     { addFeed(this.message) }],
    ['deleteFeed <Feed名>', 'Feedを削除する', /deleteFeed.*/,
     { deleteFeed(this.message) }],
    ['info', 'Spring Boot Actuatorの infoを表示', /info/,
     { actuator() }],
    ['health', 'Spring Boot Actuatorの healthを表示', /health/,
     { actuator() }],
    ['metrics', 'Spring Boot Actuatorの metricsを表示', /metrics/,
     { actuator() }],
    ['addJenkins <Jenkins Job名> <URL> [<Username> <Password>]', 'Jenkins Jobを登録する', /addJenkins.*/,
     { addJenkins(this.message) }],
    ['deleteJenkins <Jenkins Job名>', 'Jenkins Jobを削除する', /deleteJenkins.*/,
     { deleteJenkins(this.message) }],
    ['build <Jenkins Job名>', 'Jenkins Jobのビルドを依頼する', /build.*/,
     { buildByJenkins(this.message) }]
  ]

  
  void defaultHandler(ChatMessage message) {
    this.message = message
    commandList.each { commandName, desc, trigger, closure ->
      if( message.text ==~ trigger ) {
        closure.call()
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


  void displayAllConnectedUsers() {
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


  void actuator() {
    def type = this.message.text
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
      replyMessage message.username, "${key} : ${value}"
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

      replyMessage to, "repository: <a href='${url}'>${url}</a>", true, wh.hookName

      if( payload.pusher ) {
        replyMessage to, "ref: ${payload.ref}", true, wh.hookName
      }

      if( payload.issue ) {
        def issueurl = "${url}/issues/${payload.issue.number}"
        replyMessage to, "issue: <a href='${issueurl}'>No. ${payload.issue.number}</a>", true, wh.hookName
        replyMessage to, "title: ${payload.issue.title}", true, wh.hookName
      }

      if( payload.pull_request ) {
        def prurl = "${url}/pull/${payload.pull_request.number}"
        replyMessage to, "pull request: <a href='${prurl}'>No. ${payload.pull_request.number}</a>", true, wh.hookName
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

  
  void replyMessage(String to, String message,
                    boolean persistence = false,
                    String replyname = 'gkibot') {
    String replyto = "/topic/${to}"
    def msg = new ChatMessage(text: message, username: replyname)

    if (persistence) {
      msg.status = 'fixed'
      msg.chatroom = to

      log.info msg.toString()
      msg.save()
    }

    brokerMessagingTemplate.convertAndSend replyto, (msg as JSON).toString()
  }
}
