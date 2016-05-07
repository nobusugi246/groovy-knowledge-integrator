import gki.chat.ChatRoom
import gki.chat.ChatUser

class BootStrap {
  def init = { servletContext ->
    new ChatRoom(name: "Entrance").save()
  }

  def destroy = {
  }
}
