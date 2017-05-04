botsListVue = new Vue
  el: '#botsList'
  data:
    botsList: []
  created: () ->
    $.ajax
      url: '/bots'
      success: (e) ->
        botsListVue.botsList = e
  methods:
    left: (event) ->
      event.stopPropagation()
      cur = @botsList.page.number
      if cur > 0
        $.ajax
          url: "/bots?page=#{cur - 1}"
          success: (e) ->
            botsListVue.botsList = e
    right: (event) ->
      event.stopPropagation()
      cur = @botsList.page.number
      if cur < (@botsList.page.totalPages - 1)
        $.ajax
          url: "/bots?page=#{cur + 1}"
          success: (e) ->
            botsListVue.botsList = e

modalNewBot = new Vue
  el: '#newBotModal'
  data:
    name: ''
    from: 'Starter'
    botsList: []
    message: ''
  created: () ->
    $.ajax
      url: '/bots?size=1000'
      success: (e) ->
        modalNewBot.botsList = e._embedded.bots
  methods:
    close: () ->
      @message = ''
    createNewBot: () ->
      userName = $('#userName').val().trim()
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
          modalNewBot.message = e
          $.ajax
            url: '/bots'
            success: (e) ->
              botsListVue.botsList = e

# ----------------------------------------------------------------
$(() ->
  if localStorage['userName']? and $('#userName').val().trim()?
    $('#userName').val localStorage['userName']

  if $('#userName').val().trim() is ''
    $('#userName').focus()

  $('#userName').on 'keyup', ->
    localStorage['userName'] = $('#userName').val()

  $('#createNewBotButton').on 'click', ->
    $('#newBotName').focus()
    $.ajax
      url: '/bots?size=1000'
      success: (e) ->
        modalNewBot.botsList = e._embedded.bots

  editor = ace.edit("editor")
  editor.setTheme("ace/theme/monokai")
  editor.getSession().setMode("ace/mode/groovy")

  $('body').removeClass 'uk-invisible'

  console.log 'coffee main started.'
)
