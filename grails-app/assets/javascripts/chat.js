var connect, heartbeatUser, lastUser, onConnect, onDisconnect, onReceiveByUser, onReceiveChatRoom, stompClient, subscribeAll, unsubscribeAll;

stompClient = {};

lastUser = {};

$('#datetimepickerInline').datetimepicker({
  inline: true,
  locale: 'ja',
  showTodayButton: true,
  format: 'yyyy-MM-dd'
});

$('#datetimepickerInline').on('dp.change', function(event) {
  var message, selectedDate;
  selectedDate = moment(event.date).format('YYYY-MM-DD');
  if (selectedDate > moment().format('YYYY-MM-DD')) {
    return;
  }
  $('#area00').append("<hr/>\n<div class=\"row\">\n<div class=\"col-sm-11 col-sm-offset-1\">\n    <div class=\"row\">\n        <i>" + selectedDate + "</i>\n    </div>\n</div>\n</div>");
  lastUser = {};
  message = {};
  message.text = selectedDate;
  message.status = '';
  message.chatroom = $('#chatRoomSelected').val();
  message.username = $('#userName').val();
  stompClient.send("/app/log", {}, JSON.stringify(message));
  return $('#chatMessage').focus();
});

setInterval(function() {
  return $('#currentTime').text(moment().format('MM/DD HH:mm'));
}, 1000);

$('#userName').on('keyup', function() {
  return localStorage['userName'] = $('#userName').val();
});

subscribeAll = function() {
  stompClient.subscribe("/topic/" + ($('#userName').val()), function(message) {
    return onReceiveByUser(message);
  });
  return stompClient.subscribe("/topic/" + ($('#chatRoomSelected').val()), function(message) {
    return onReceiveChatRoom(message);
  });
};

unsubscribeAll = function() {
  return _.each(_.allKeys(stompClient.subscriptions), function(it) {
    return stompClient.unsubscribe(it);
  });
};

$('#userName').focusout(function() {
  var message;
  if ($('#userName').val() !== '') {
    unsubscribeAll();
    subscribeAll();
    message = {};
    message.text = '';
    message.status = '';
    message.chatroom = $('#chatRoomSelected').val();
    message.username = $('#userName').val();
    return stompClient.send("/app/updateUser", {}, JSON.stringify(message));
  }
});

$('#chatRoomSelected').on('change', function(event) {
  var message;
  $('#area00').html('');
  lastUser = '';
  unsubscribeAll();
  subscribeAll();
  message = {};
  message.text = moment().format('YYYY-MM-DD');
  message.status = '';
  message.chatroom = $('#chatRoomSelected').val();
  message.username = $('#userName').val();
  stompClient.send("/app/updateUser", {}, JSON.stringify(message));
  stompClient.send("/app/log", {}, JSON.stringify(message));
  return $('#chatMessage').focus();
});

$('#chatMessage').on('keyup', function(event) {
  var message;
  if (event.keyCode === 13 && $.trim($('#chatMessage').val()) !== '') {
    message = {};
    message.text = _.escape($.trim($('#chatMessage').val()));
    message.status = 'fixed';
    message.chatroom = $('#chatRoomSelected').val();
    message.username = $('#userName').val();
    stompClient.send("/app/message", {}, JSON.stringify(message));
    $('#chatMessage').val('');
    return $('#chatMessage').focus();
  }
});

heartbeatUser = function() {
  var message;
  message = {};
  message.text = '';
  message.status = 'heartbeat';
  message.chatroom = $('#chatRoomSelected').val();
  message.username = $('#userName').val();
  return stompClient.send("/app/heartbeat", {}, JSON.stringify(message));
};

setInterval(function() {
  return heartbeatUser();
}, 3000);

onReceiveByUser = function(message) {
  var crlDef, msg, selectedDate, tableDef;
  console.log(("@" + ($('#userName').val()) + ": ") + message.body);
  msg = JSON.parse(message.body);
  if (msg.chatRoomList) {
    crlDef = '';
    _.each(msg.chatRoomList, function(it) {
      return crlDef += "<option value='" + it.id + "'>" + it.name + "</option>";
    });
    $('#chatRoomSelected').html(crlDef);
    return $('#chatRoomSelected').val(msg.selected);
  } else if (msg.userList) {
    tableDef = "<table class=\"table table-striped\">\n<thead>\n  <tr>\n    <th>ID</th>\n    <th>User Name</th>\n  </tr>\n</thead>\n<tbody>";
    _.each(msg.userList, function(it) {
      return tableDef += "<tr>\n  <td style=\"width: 4em;\">" + it.id + "</td>\n  <td>" + it.username + "</td>\n</tr>";
    });
    tableDef += "</tbody>\n</table>";
    return $('#connectedUsersTable').html(tableDef);
  } else if (msg.status === 'closeTime') {
    selectedDate = $('#datetimepickerInline').data('DateTimePicker').date().format('YYYY-MM-DD');
    $('#area00').append("<div class=\"row\">\n<div class=\"col-sm-11 col-sm-offset-1\">\n    <div class=\"row\">\n        <i>" + selectedDate + "</i>\n    </div>\n</div>\n</div>\n<hr/>");
    return $('#area00').scrollTop(($("#area00")[0].scrollHeight));
  } else {
    return onReceiveChatRoom(message);
  }
};

onReceiveChatRoom = function(message) {
  var msg;
  console.log("Chat Message: " + message.body);
  msg = JSON.parse(message.body);
  if (msg.text.match(/^http:\/\/.+/)) {
    msg.text = "<a href='" + msg.text + "'>" + msg.text + "</a>";
  }
  if (lastUser === msg['username']) {
    $('#area00').append("<div class=\"row\">\n<div class=\"col-sm-11 col-sm-offset-1\">\n    <div class=\"row\" id=\"message" + msg['id'] + "\" data-toggle=\"tooltip\"\n         data-placement=\"left\" title=\"" + msg['time'] + "\">\n        " + msg['text'] + "\n    </div>\n</div>\n</div>");
  } else {
    $('#area00').append("<div class=\"row\">\n<div class=\"col-sm-1\" align=\"center\">\n    <svg width=\"40\" height=\"40\" id=\"identicon" + msg['id'] + "\"></svg>\n</div>\n<div class=\"col-sm-11\">\n    <div class=\"row\">\n        <strong>" + msg['username'] + "</strong>&nbsp;<i>" + msg.time + "</i>\n    </div>\n    <div class=\"row\" id=\"message" + msg['id'] + "\" data-toggle=\"tooltip\"\n         data-placement=\"left\" title=\"" + msg['time'] + "\">\n        " + msg['text'] + "\n    </div>\n</div>\n</div>");
    jdenticon.update("#identicon" + msg['id'], sha1(msg['username']));
  }
  $('#area00').scrollTop(($("#area00")[0].scrollHeight));
  lastUser = msg['username'];
  if (msg['id'] != null) {
    return $("#message" + msg['id']).tooltip();
  }
};

onConnect = function(frame) {
  var message;
  subscribeAll();
  $('#wsstatus').removeClass('label-danger');
  $('#wsstatus').addClass('label-info');
  $('#wsstatus').html('OnLine');
  if ($('#userName').val() !== '') {
    message = {};
    message.text = moment().format('YYYY-MM-DD');
    message.status = '';
    message.chatroom = $('#chatRoomSelected').val();
    message.username = $('#userName').val();
    return stompClient.send('/app/addUser', {}, JSON.stringify(message));
  }
};

onDisconnect = function(frame) {
  $('#wsstatus').removeClass('label-info');
  $('#wsstatus').addClass('label-danger');
  return $('#wsstatus').html('OffLine');
};

connect = function() {
  var client, socket;
  socket = new SockJS('/stomp');
  client = Stomp.over(socket);
  client.connect({}, function(frame) {
    stompClient = client;
    stompClient.debug = null;
    return onConnect(frame);
  });
  return client.disconnect({}, function(frame) {
    return onDisconnect(frame);
  });
};

$(document).ready(function() {
  if ((localStorage['userName'] != null) && ($('#userName').val() != null)) {
    $('#userName').val(localStorage['userName']);
    return connect();
  }
});
