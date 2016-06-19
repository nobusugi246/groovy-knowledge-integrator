# WebSocket Stomp Client
stompClient = {}
lastUser = {}
lastMessage = ''

# display DateTimePicker inline
$('#datetimepickerInline').datetimepicker({
    inline: true
    locale: 'ja'
    showTodayButton: true
    format: 'yyyy-MM-dd'
    # dayViewHeaderFormat: 'M月 YYYY年'
})


# DateTimePicker changed eventhandler
$('#datetimepickerInline').on 'dp.change', (event)->
    selectedDate = moment(event.date).format('YYYY-MM-DD')

    if selectedDate > moment().format('YYYY-MM-DD')
        return

    $('#area00').append """
    <hr/>
    <div class="row" style="background-color: lightgreen;">
    <div class="col-sm-11 col-sm-offset-1">
        <div class="row">
            <i>#{selectedDate}</i>
        </div>
    </div>
    </div>
    """

    lastUser = {}
    message = {}
    message.text = selectedDate
    message.status = ''
    message.chatroom = $('#chatRoomSelected').val()
    message.username = $('#userName').val()

    stompClient.send "/app/log", {}, JSON.stringify(message)
    $('#chatMessage').focus()
    $('#area00').scrollTop(($("#area00")[0].scrollHeight))


# update Date, Time
setInterval ->
        $('#currentTime').text moment().format('MM/DD HH:mm')
    , 1000


# store userName into localStorage
$('#userName').on 'keyup', ->
    localStorage['userName'] = $('#userName').val()


# subscribe UserName and ChatRoom
subscribeAll = () ->
    stompClient.subscribe "/topic/#{$('#userName').val()}", (message) ->
        onReceiveByUser(message)

    stompClient.subscribe "/topic/#{$('#chatRoomSelected').val()}", (message) ->
        onReceiveChatRoom(message)


# unsubscribe all subscriptions
unsubscribeAll = () ->
    _.each _.allKeys(stompClient.subscriptions), (it) ->
        stompClient.unsubscribe it


# update userName
$('#userName').focusout ->
    if $('#userName').val() isnt ''
        unsubscribeAll()
        subscribeAll()

        message = {}
        message.text = ''
        message.status = ''
        message.chatroom = $('#chatRoomSelected').val()
        message.username = $('#userName').val()

        stompClient.send "/app/updateUser", {}, JSON.stringify(message)


# update ChatRoomSelected
$('#chatRoomSelected').on 'change', (event) ->
    $('#area00').html ''
    lastUser = ''

    unsubscribeAll()
    subscribeAll()

    message = {}
    message.text = moment().format('YYYY-MM-DD')
    message.status = ''
    message.chatroom = $('#chatRoomSelected').val()
    message.username = $('#userName').val()

    stompClient.send "/app/updateUser", {}, JSON.stringify(message)
    stompClient.send "/app/log", {}, JSON.stringify(message)
    $('#chatMessage').focus()
    $("a[title='Go to today']").click()


# send chat message
$('#chatMessage').on 'keyup', (event) ->
    if event.keyCode is 73 and event.ctrlKey is true  # Ctrl + i
        $('#chatMessage').val lastMessage
    else if event.keyCode is 13 and $.trim($('#chatMessage').val()) isnt ''
        message = {}
        message.text = _.escape($.trim($('#chatMessage').val()))
        message.status = 'fixed'
        message.chatroom = $('#chatRoomSelected').val()
        message.username = $('#userName').val()

        stompClient.send "/app/message", {}, JSON.stringify(message)
        $('#chatMessage').val ''
        $('#chatMessage').focus()
        lastMessage = message.text


# heartbeat user
heartbeatUser = () ->
    message = {}
    message.text = ''
    message.status = 'heartbeat'
    message.chatroom = $('#chatRoomSelected').val()
    message.username = $('#userName').val()

    stompClient.send "/app/heartbeat", {}, JSON.stringify(message)


setInterval ->
        heartbeatUser()
    , 3000

    
# WebSocket user message receive eventhandler
onReceiveByUser = (message) ->
    console.log "@#{$('#userName').val()}: " +  message.body
    msg = JSON.parse(message.body)

    if msg.chatRoomList
        crlDef = ''
        _.each msg.chatRoomList, (it) ->
            crlDef += "<option value='#{it.id}'>#{it.name}</option>"
        $('#chatRoomSelected').html crlDef
        $('#chatRoomSelected').val msg.selected
    else if msg.userList
        tableDef = """<table class="table table-striped">
            <thead>
              <tr>
                <th>ID</th>
                <th>User Name</th>
              </tr>
            </thead>
            <tbody>"""

        _.each msg.userList, (it) ->
            tableDef += """<tr>
                  <td style="width: 4em;">#{it.id}</td>
                  <td>#{it.username}</td>
                </tr>"""

        tableDef += """</tbody>
          </table>"""

        $('#connectedUsersTable').html tableDef
    else if msg.status is 'closeTime'
        selectedDate = $('#datetimepickerInline').data('DateTimePicker').date().format('YYYY-MM-DD')
        $('#area00').append """
        <div class="row" style="background-color: lightgreen;">
        <div class="col-sm-11 col-sm-offset-1">
            <div class="row">
                <i>#{selectedDate}</i>
            </div>
        </div>
        </div>
        <hr/>
        """
        $('#area00').scrollTop(($("#area00")[0].scrollHeight))
    else
        onReceiveChatRoom(message)


# WebSocket chat message receive eventhandler
onReceiveChatRoom = (message) ->
    console.log "Chat Message: " + message.body
    msg = JSON.parse(message.body)

    if msg.text.match /^https{0,1}:\/\/.+/
        msg.text = "<a href='#{msg.text}'>#{msg.text}</a>"

    if lastUser is msg['username']
        $('#area00').append """
        <div class="row">
        <div class="col-sm-11 col-sm-offset-1">
            <div class="row" id="message#{msg['id']}" data-toggle="tooltip"
                 data-placement="left" title="#{msg['time']}">
                #{msg['text']}
            </div>
        </div>
        </div>
        """
    else
        $('#area00').append """
        <div class="row">
        <div class="col-sm-1" align="center">
            <svg width="40" height="40" id="identicon#{msg['id']}"></svg>
        </div>
        <div class="col-sm-11">
            <div class="row">
                <strong>#{msg['username']}</strong>&nbsp;<i>#{msg.time}</i>
            </div>
            <div class="row" id="message#{msg['id']}" data-toggle="tooltip"
                 data-placement="left" title="#{msg['time']}">
                #{msg['text']}
            </div>
        </div>
        </div>
        """
        jdenticon.update("#identicon#{msg['id']}", sha1(msg['username']))

    $('#area00').scrollTop(($("#area00")[0].scrollHeight))
    lastUser = msg['username']

    if msg['id']?
        $("#message#{msg['id']}").tooltip()
    
    
# WebSocket connect eventhandler
onConnect = () ->
    subscribeAll()

    $('#wsstatus').removeClass 'label-danger'
    $('#wsstatus').addClass 'label-info'
    $('#wsstatus').html 'OnLine'

    if $('#userName').val() isnt ''
        message = {}
        message.text = moment().format('YYYY-MM-DD')
        message.status = ''
        message.chatroom = $('#chatRoomSelected').val()
        message.username = $('#userName').val()

        stompClient.send '/app/addUser', {}, JSON.stringify(message)
        

# WebSocket disconnect eventhandler
onDisconnect = () ->
    $('#wsstatus').removeClass 'label-info'
    $('#wsstatus').addClass 'label-danger'
    $('#wsstatus').html 'OffLine'


# connect to WebSocket Server
connect = () ->
    # Connect WebSocket
    socket = new SockJS '/stomp'
    client = Stomp.over socket

    # WebSocket Connected
    client.connect {}, () ->
        stompClient = client
        stompClient.debug = null
        onConnect()
    , () ->
        onDisconnect()

    
# initialize display
$(document).ready ->
    # Display username from localStorage if exists.
    if localStorage['userName']? and $('#userName').val()?
        $('#userName').val localStorage['userName']

        connect()
