import gki.chat.ChatRoom
import gki.chat.ChatUser

class BootStrap {
  def init = { servletContext ->
    if ( !ChatRoom.get(1) ) {
      new ChatRoom(name: "Entrance").save()
    }
  }

  def destroy = {
  }
}
