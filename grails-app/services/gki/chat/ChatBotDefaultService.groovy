package gki.chat

import grails.converters.JSON
import groovy.util.logging.Slf4j
import grails.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import groovy.xml.XmlUtil

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
    ['hello', '利用方法の説明(このメッセージ)', /(こんにちは|今日は|hello|help)/,
     { hello(this.message.username) }],
    ['makeChatRoom <ChatRoom名>', 'ChatRoomの作成', /makeChatRoom .+/,
     { makeChatRoom(this.message) }],
    ['deleteChatRoom <ChatRoom名>', 'ChatRoomの削除', /deleteChatRoom .+/,
     { deleteChatRoom(this.message) }],
    ['users', '接続している全ユーザのリストを表示', /users/,
     { displayAllConnectedUsers() }],
    ['info', 'Spring Boot Actuatorの infoを表示', /info/,
     { actuator() }],
    ['health', 'Spring Boot Actuatorの healthを表示', /health/,
     { actuator() }],
    ['metrics', 'Spring Boot Actuatorの metricsを表示', /metrics/,
     { actuator() }]
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

    def messageList = ChatMessage.findAllByUsername(username, [sort: 'id'])
    if( messageList.size >= 2 ){
      def lastMessage = messageList[-2]
            
      replyMessage username,
                   "あなたの最新のメッセージは"
      replyMessage username,
                   "${lastMessage.date} ${lastMessage.time} '${lastMessage.text}'"
      replyMessage username,
                   "です。"
    }

    replyMessage username,
                 "このチャットシステムで利用できるコマンドは以下です。"

    commandList.each { commandName, desc, trigger, closure ->
      log.info commandName
      replyMessage username, "&nbsp; &nbsp; ${XmlUtil.escapeXml commandName}: ${XmlUtil.escapeXml desc}"
      Thread.sleep(20)
    }    
  }


  void makeChatRoom(ChatMessage message) {
    this.message = message
    def words = message.text.split(' ')
    if ( words.size() != 2) {
      replyMessage message.chatroom,
                   "${message.username}さん, ChatRoomの指定が正しくありません。",
                   true
    } else {
      if (ChatRoom.findByName(words[1])) {
        replyMessage message.chatroom,
                     "${message.username}さん, ChatRoom '${words[1]}'は既にあります。",
                     true
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
    this.message = message
    def words = message.text.split(' ')
    if ( words.size() != 2) {
      replyMessage message.chatroom,
                   "${message.username}さん, ChatRoomの指定が正しくありません。",
                   true
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
                     "${message.username}さん, ChatRoom '${words[1]}'は有りません。",
                     true
      }
    }
  }


  void displayAllConnectedUsers() {
    def userList = ChatUser.findAllWhere(enabled: true)

    replyMessage message.username,
                 "${message.username}さん, 接続中のユーザは ${userList.size}名です。"
    userList.each { user ->
      def chatroom = ChatRoom.get(user.chatroom)
      replyMessage message.username,
                   "${user.username}さんが '${chatroom.name}' にいます。"
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
  

  void webhook(payload) {
    def url = payload.repository.html_url

    def roomList = ChatRoom.findAll()

    roomList.each { room ->
      def to = room.id as String

      if( payload.pusher ) {
        replyMessage to, "レポジトリに Pushされました。", true
      } else if( payload.issue ) {
        if( payload.action == 'opened' ) {
          replyMessage to, "レポジトリに Issueが作成されました。", true
        } else if( payload.action == 'closed' ) {
          replyMessage to, "レポジトリの Issueがクローズされました。", true
        } else if( payload.action == 'reopened' ) {
          replyMessage to, "レポジトリの Issueが再開されました。", true
        } else if( payload.action == 'created' && payload.comment ) {
          replyMessage to, "レポジトリの Issueにコメントが追加されました。", true
        }
      } else if( payload.pull_request ) {
        if( payload.action == 'opened' ) {
          replyMessage to, "レポジトリに Pull Requestが作成されました。", true
        } else if( payload.action == 'closed' ) {
          replyMessage to, "レポジトリの Pull Requestがクローズされました。", true
        } else if( payload.action == 'reopened' ) {
          replyMessage to, "レポジトリの Pull Requestが再開されました。", true
        }
      }

      replyMessage to, "repository: <a href='${url}'>${url}</a>", true

      if( payload.pusher ) {
        replyMessage to, "ref: ${payload.ref}", true
      }

      if( payload.issue ) {
        def issueurl = "${url}/issues/${payload.issue.number}"
        replyMessage to, "issue: <a href='${issueurl}'>No. ${payload.issue.number}</a>", true
        replyMessage to, "title: ${payload.issue.title}", true
      }

      if( payload.pull_request ) {
        def prurl = "${url}/pull/${payload.pull_request.number}"
        replyMessage to, "pull request: <a href='${prurl}'>No. ${payload.pull_request.number}</a>", true
        replyMessage to, "title: ${payload.pull_request.title}", true
      }

      if( payload.comment ) {
        replyMessage to, "comment: ${payload.comment.body}", true
      }

      if( payload.sender ) {
        replyMessage to, "by : ${payload.sender.login}", true
      }
    }
  }

  
  void replyMessage(String to, String message, boolean persistence = false) {
    String replyto = "/topic/${to}"
    def msg = new ChatMessage(text: message, username: 'gkibot')

    if (persistence) {
      msg.status = 'fixed'
      msg.chatroom = to

      log.info msg.toString()
      msg.save()
    }

    brokerMessagingTemplate.convertAndSend replyto, (msg as JSON).toString()
  }
}
