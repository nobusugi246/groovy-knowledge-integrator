package gki.chat

import grails.converters.JSON
import groovy.util.logging.Slf4j
import grails.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import groovy.xml.XmlUtil

@Slf4j
@Transactional
class ChatBotDefaultService {

  def chatService
  SimpMessagingTemplate brokerMessagingTemplate

  ChatMessage message
  
  def commandList = [
    ['hello', '利用方法の説明', /(こんにちは|今日は|hello|help)/,
     { hello(this.message.username) }],
    ['makeChatRoom <ChatRoom名>', 'ChatRoomの作成', /makeChatRoom .+/,
     { makeChatRoom(this.message) }],
    ['deleteChatRoom <ChatRoom名>', 'ChatRoomの削除', /deleteChatRoom .+/,
     { deleteChatRoom(this.message) }],
    ['users', '接続している全ユーザのリストを表示', /users/,
     { displayAllConnectedUsers() }]
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
      replyMessage username, "${XmlUtil.escapeXml commandName}: ${XmlUtil.escapeXml desc}"
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
  
  
  void replyMessage(String to, String message, boolean persistence = false) {
    String replyto = "/topic/${to}"
    def msg = new ChatMessage(text: message, username: 'gkibot')

    if (persistence) {
      msg.status = 'fixed'
      msg.chatroom = this.message.chatroom

      log.info msg.toString()
      msg.save()
    }

    brokerMessagingTemplate.convertAndSend replyto, (msg as JSON).toString()
  }
}
