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

botEditorVue = new Vue
  el: '#botEditor'
  data:
    bot: {}
    visible: false
    editor: {}
  mounted: () ->
    @editor = ace.edit("editor")
    @editor.setTheme("ace/theme/monokai")
    @editor.getSession().setMode("ace/mode/groovy")
    @setup(1)
  computed:
    formatedCreatedDate: () ->
      @bot.createdDate?.replace('T',' ').substring(0, 19)
    formatedUpdatedDate: () ->
      @bot.updatedDate?.replace('T',' ').substring(0, 19)
  methods:
    setup: (botId) ->
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
    toggle: () ->
      @bot.revisedBy = sideMenuVue.userName
      @bot.updatedDate = moment().format()
      @bot.enabled = not @bot.enabled
      $.ajax
        method: 'PUT'
        contentType: 'application/json'
        url: @bot._links.self.href
        data: JSON.stringify @bot
        success: (e) ->
          UIkit.notification
            message: "Enabled: #{e.enabled}."
            status: 'success'
            pos: 'bottom-center'
            timeout: 2000
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
            message: 'saved.'
            status: 'success'
            pos: 'bottom-center'
            timeout: 2000

# ----------------------------------------------------------------
$(() ->
  $('body').removeClass 'uk-invisible'

  console.log 'coffee main started.'
)
