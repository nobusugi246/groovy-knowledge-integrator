stompClient = {}

# DateTimePickerの表示
$('#datetimepickerInline').datetimepicker({
    inline: true
    locale: 'ja'
    showTodayButton: true
    format: 'yyyy-MM-dd'
})


# DateTimePicker changed
$('#datetimepickerInline').on 'dp.change', (event)->
    console.log 'DateTimePicker: ' + moment(event.date).format('YYYY-MM-DD')


# update Date, Time
setInterval ->
        $('#currentTime').text moment().format('MM/DD HH:mm:ss')
    , 1000


# store userName into localStorage
$('#userName').on 'keyup', ->
    localStorage['userName'] = $('#userName').val()


# send chat message
$('#chatMessage').on 'keyup', (event) ->
    if event.keyCode is 13 and $('#chatMessage').val() isnt ''
        message = {}
        message.text = $('#chatMessage').val()
        message.status = ''
        message.sendto = $('#chatRoomSelected').val()
        message.username = $('#userName').val()

        stompClient.send "/app/message", {}, JSON.stringify(message)
        $('#chatMessage').val ''
        
    
# WebSocket user message receive eventhandler
onReceiveByUser = (message) ->
    console.log "@#{}: " +  message.body


# WebSocket result message receive eventhandler
onReceiveResult = (message) ->
    console.log 'Result: ' +  message.body


# WebSocket chat message receive eventhandler
onReceiveChatRoom = (message) ->
    console.log "Chat Message: " + message.body


# WebSocket connect eventhandler
onConnect = (frame) ->
    $('#wsstatus').removeClass 'label-danger'
    $('#wsstatus').addClass 'label-info'
    $('#wsstatus').html 'OnLine'

    stompClient.send '/app/addUser', {}, $('#userName').val()

    stompClient.subscribe "/topic/result", (message) ->
        onReceiveResult(message)

    stompClient.subscribe "/topic/#{$('#userName').val()}", (message) ->
        onReceiveByUser(message)

    stompClient.subscribe "/topic/#{$('#chatRoomSelected').val()}", (message) ->
        onReceiveChatRoom(message)
        

# WebSocket disconnect eventhandler
onDisconnect = (frame) ->
    $('#wsstatus').removeClass 'label-info'
    $('#wsstatus').addClass 'label-danger'
    $('#wsstatus').html 'OffLine'

    
# initialize display
$(document).ready ->
    # Display username from localStorage if exists.
    if localStorage['userName']? and $('#userName').val()?
        $('#userName').val localStorage['userName']

        # Connect WebSocket
        socket = new SockJS '/stomp'
        client = Stomp.over socket

        # WebSocket Connected
        client.connect {}, (frame) ->
            stompClient = client
            stompClient.debug = null
            onConnect(frame)

        # WebSocket DisConnected
        client.disconnect {}, (frame) ->
            onDisconnect(frame)

