import gki.chat.ChatRoom
import gki.chat.ChatUser

class BootStrap {
  def init = { servletContext ->
    (0..9).each {
      new ChatUser(username: "user${it}", role: "User").save()
    }

    new ChatRoom(name: "Entrance").save()
  }

  def destroy = {
  }
}
