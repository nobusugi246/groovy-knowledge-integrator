serverErrorHandler = (xhr, msg, ext) ->
  UIkit.notification
    message: "Server #{msg}: status = #{xhr.status}"
    status: 'danger'
    pos: 'bottom-center'
    timeout: 2000

sideMenuVue = new Vue
  el: '#sideMenu'
  data:
    userName: ''
  created: () ->
    @userName = localStorage['userName']
  updated: () ->
    localStorage['userName'] = @userName
  methods:
    createNewBot: () ->
      modalNewBotVue.updateBotsList()

chatServersListVue = new Vue
  el: '#chatServersList'
  data:
    chatServersList: {}
    chatServers: {}
    count: 0
    page: 0
    newName: ''
    newUrl: ''
  created: () ->
    @updateChatServersList(0)
  watch:
    page: (page) ->
      @updateChatServersList(page)
    chatServersList: (val) ->
      @chatServers = val._embedded.chatServers
      _.each @chatServers, (server) ->
        server.id = _.last server?._links.self.href.split('/')
      @count = val.page.totalElements
  methods:
    deleteChatServer: (id) ->
      $.ajax
        method: 'DELETE'
        url: "/chatServers/#{id}"
        success: (e) ->
          chatServersListVue.updateChatServersList(chatServersListVue.page)
          UIkit.notification
            message: "delete ChatServer."
            status: 'success'
            pos: 'top-center'
            timeout: 2000
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    newChatServer: () ->
      if @newName is '' or @newUrl is ''
        return
      console.log "#{@newName} #{@newUrl}"
      chatServer = {}
      chatServer.name = @newName
      chatServer.url = @newUrl
      chatServer.enabled = true
      @newName = ''
      @newUrl = ''
      $.ajax
        method: 'POST'
        contentType: 'application/json'
        url: '/chatServers'
        data: JSON.stringify chatServer
        success: (e) ->
          chatServersListVue.updateChatServersList(chatServersListVue.page)
          UIkit.notification
            message: "add ChatServer."
            status: 'success'
            pos: 'top-center'
            timeout: 2000
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    toggle: (id) ->
      $.ajax
        url: "/chatServers/#{id}"
        success: (e) ->
          chatServer = {}
          chatServer.id = e.id
          chatServer.name = e.name
          chatServer.url = e.url
          chatServer.enabled = not e.enabled
          $.ajax
            method: 'PUT'
            contentType: 'application/json'
            url: e._links.self.href
            data: JSON.stringify chatServer
            success: (e) ->
              chatServersListVue.updateChatServersList(0)
              UIkit.notification
                message: "Enabled: #{e.enabled}."
                status: 'success'
                pos: 'top-center'
                timeout: 2000
            error: (xhr, msg, ext) ->
              serverErrorHandler(xhr, msg, ext)
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    left: (event) ->
      event.stopPropagation()
      cur = @chatServersList.page.number
      if cur > 0
        @page = cur - 1
    right: (event) ->
      event.stopPropagation()
      cur = @chatServersList.page.number
      if cur < (@chatServersList.page.totalPages - 1)
        @page = cur + 1
    updateChatServersList: (page) ->
      $.ajax
        url: "/chatServers?page=#{page}&size=10"
        success: (e) ->
          chatServersListVue.chatServersList = e
          chatServersListVue.page = e.page.number
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
  computed:
    pageLeft: () ->
      @chatServersList?.page?.number > 0
    pageRight: () ->
      @chatServersList?.page?.number+1 < @chatServersList?.page?.totalPages

botsListVue = new Vue
  el: '#botsList'
  data:
    botsList: {}
    bots: []
    count: 0
    page: 0
  created: () ->
    @updateBotsList(0)
  watch:
    page: (page) ->
      @updateBotsList(page)
    botsList: (val) ->
      @bots = val._embedded.bots
      _.each @bots, (bot) ->
        bot.id = _.last bot._links.self.href.split('/')
      @count = val.page.totalElements
  methods:
    edit: (botId) ->
      botEditorVue.setup(botId)
    left: (event) ->
      event.stopPropagation()
      cur = @botsList.page.number
      if cur > 0
        @page = cur - 1
    right: (event) ->
      event.stopPropagation()
      cur = @botsList.page.number
      if cur < (@botsList.page.totalPages - 1)
        @page = cur + 1
    updateBotsList: (page) ->
      $.ajax
        url: "/bots?page=#{page}"
        success: (e) ->
          botsListVue.botsList = e
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
  computed:
    pageLeft: () ->
      @botsList.page?.number > 0
    pageRight: () ->
      @botsList.page?.number+1 < @botsList.page?.totalPages

modalNewBotVue = new Vue
  el: '#newBotModal'
  data:
    name: ''
    from: 'Starter'
    botsList: []
    bots: []
    message: ''
  methods:
    close: () ->
      @message = ''
      @name = ''
      @from = 'Starter'
    updateBotsList: () ->
      $.ajax
        url: '/bots?size=10000'
        success: (e) ->
          modalNewBotVue.botsList = e._embedded.bots
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    createNewBot: () ->
      userName = sideMenuVue.userName
      if !userName
        @message = 'Please set your name.'
        return
      if !@name
        @message = 'Please set new bot name.'
        return
      $.ajax
        method: 'POST'
        url: '/create'
        data:
          'name': @name
          'from': @from
          'user': userName
        success: (e) ->
          modalNewBotVue.message = e
          $.ajax
            url: '/bots'
            success: (e) ->
              botsListVue.botsList = e
              modalNewBotVue.updateBotsList()
            error: (xhr, msg, ext) ->
              serverErrorHandler(xhr, msg, ext)
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)

botEditorVue = new Vue
  el: '#botEditor'
  data:
    bot: {}
    visible: false
    editor: {}
    testMessage: ''
  mounted: () ->
    ace.require("ace/ext/language_tools")
    @editor = ace.edit("editor")
    @editor.setOptions
      enableBasicAutocompletion: true
      enableLiveAutocompletion: true
      showPrintMargin: false
#      maxLines: Infinity
      fontSize: "85%"
    @editor.setTheme("ace/theme/monokai")
    @editor.getSession().setMode("ace/mode/groovy")
#    @setup(1)
  computed:
    formatedCreatedDate: () ->
      @bot.createdDate?.replace('T',' ').substring(0, 19)
    formatedUpdatedDate: () ->
      @bot.updatedDate?.replace('T',' ').substring(0, 19)
  methods:
    setup: (botId) ->
      @testMessage = ''
      $('#testResult').html ''
      $.ajax
        url: "/bots/#{botId}"
        success: (e) ->
          botEditorVue.bot = e
          botEditorVue.bot.script = e.script
          botEditorVue.bot.createdDate = moment(e.createdDate).format()
          botEditorVue.bot.updatedDate = moment(e.updatedDate).format()
          if botEditorVue.bot.updatedDate is 'Invalid date'
            botEditorVue.bot.updatedDate = ''
          botEditorVue.editor.$blockScrolling = Infinity
          b64Decoded = base64js.toByteArray(e.script)
          decoded = new TextDecoderLite('utf-8').decode(b64Decoded)
          botEditorVue.editor.setValue decoded
          botEditorVue.editor.gotoLine(0)
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    toggleE: () ->
      @bot.revisedBy = sideMenuVue.userName
      @bot.updatedDate = moment().format()
      @bot.enabled = not @bot.enabled
      $.ajax
        method: 'PUT'
        contentType: 'application/json'
        url: @bot._links.self.href
        data: JSON.stringify @bot
        success: (e) ->
          botsListVue.updateBotsList(botsListVue.page)
          UIkit.notification
            message: "Enabled: #{e.enabled}."
            status: 'success'
            pos: 'top-center'
            timeout: 2000
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    toggleAA: () ->
      @bot.revisedBy = sideMenuVue.userName
      @bot.updatedDate = moment().format()
      @bot.acceptAll = not @bot.acceptAll
      $.ajax
        method: 'PUT'
        contentType: 'application/json'
        url: @bot._links.self.href
        data: JSON.stringify @bot
        success: (e) ->
          UIkit.notification
            message: "Accept All: #{e.acceptAll}."
            status: 'success'
            pos: 'top-center'
            timeout: 2000
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    deleteBot: () ->
      if @bot.name is 'Starter'
        UIkit.notification
          message: "Can't delete 'Starter'."
          status: 'warning'
          pos: 'top-center'
          timeout: 2000
        return
      $.ajax
        method: 'DELETE'
        url: @bot._links.self.href
        success: (e) ->
          botsListVue.updateBotsList(botsListVue.page)
          botEditorVue.setup(1)
          UIkit.notification
            message: "Bot deleted."
            status: 'success'
            pos: 'top-center'
            timeout: 2000
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    test: () ->
      $('#testResult').html ''
      encoded = new TextEncoderLite('utf-8').encode(@editor.getValue())
      b64Encoded = base64js.fromByteArray(encoded)
      $.ajax
        method: 'POST'
        contentType: 'application/json'
        url: '/testBot'
        data: JSON.stringify({ 'botname':@bot.name, 'script': b64Encoded, 'message': @testMessage })
        success: (e) ->
          console.log e
          $('#testResult').append e
          UIkit.notification
            message: 'Tested.'
            status: 'success'
            pos: 'bottom-right'
            timeout: 2000
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)
    save: () ->
      @bot.revision++
      encoded = new TextEncoderLite('utf-8').encode(@editor.getValue())
      b64Encoded = base64js.fromByteArray(encoded)
      @bot.script = b64Encoded
      @bot.revisedBy = sideMenuVue.userName
      @bot.updatedDate = moment().format()
      $.ajax
        method: 'PUT'
        contentType: 'application/json'
        url: @bot._links.self.href
        data: JSON.stringify @bot
        success: (e) ->
          botsListVue.updateBotsList(botsListVue.page)
          UIkit.notification
            message: 'Saved.'
            status: 'success'
            pos: 'bottom-right'
            timeout: 2000
        error: (xhr, msg, ext) ->
          serverErrorHandler(xhr, msg, ext)

# ----------------------------------------------------------------
$(() ->
  $('body').removeClass 'uk-invisible'

  console.log 'coffee main started.'
)
