// Generated by CoffeeScript 1.12.5
(function() {
  var botEditorVue, botsListVue, chatServersListVue, modalNewBotVue, serverErrorHandler, sideMenuVue;

  serverErrorHandler = function(xhr, msg, ext) {
    return UIkit.notification({
      message: "Server " + msg + ": status = " + xhr.status,
      status: 'danger',
      pos: 'bottom-center',
      timeout: 2000
    });
  };

  sideMenuVue = new Vue({
    el: '#sideMenu',
    data: {
      userName: ''
    },
    created: function() {
      return this.userName = localStorage['userName'];
    },
    updated: function() {
      return localStorage['userName'] = this.userName;
    },
    methods: {
      createNewBot: function() {
        return modalNewBotVue.updateBotsList();
      }
    }
  });

  chatServersListVue = new Vue({
    el: '#chatServersList',
    data: {
      chatServersList: {},
      chatServers: {},
      count: 0,
      page: 0,
      newName: '',
      newUrl: ''
    },
    created: function() {
      return this.updateChatServersList(0);
    },
    watch: {
      page: function(page) {
        return this.updateChatServersList(page);
      },
      chatServersList: function(val) {
        this.chatServers = val._embedded.chatServers;
        _.each(this.chatServers, function(server) {
          return server.id = _.last(server != null ? server._links.self.href.split('/') : void 0);
        });
        return this.count = val.page.totalElements;
      }
    },
    methods: {
      deleteChatServer: function(id) {
        return $.ajax({
          method: 'DELETE',
          url: "/chatServers/" + id,
          success: function(e) {
            chatServersListVue.updateChatServersList(chatServersListVue.page);
            return UIkit.notification({
              message: "delete ChatServer.",
              status: 'success',
              pos: 'top-center',
              timeout: 2000
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      newChatServer: function() {
        var chatServer;
        if (this.newName === '' || this.newUrl === '') {
          return;
        }
        console.log(this.newName + " " + this.newUrl);
        chatServer = {};
        chatServer.name = this.newName;
        chatServer.url = this.newUrl;
        chatServer.enabled = true;
        this.newName = '';
        this.newUrl = '';
        return $.ajax({
          method: 'POST',
          contentType: 'application/json',
          url: '/chatServers',
          data: JSON.stringify(chatServer),
          success: function(e) {
            chatServersListVue.updateChatServersList(chatServersListVue.page);
            return UIkit.notification({
              message: "add ChatServer.",
              status: 'success',
              pos: 'top-center',
              timeout: 2000
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      toggle: function(id) {
        return $.ajax({
          url: "/chatServers/" + id,
          success: function(e) {
            var chatServer;
            chatServer = {};
            chatServer.id = e.id;
            chatServer.name = e.name;
            chatServer.url = e.url;
            chatServer.enabled = !e.enabled;
            return $.ajax({
              method: 'PUT',
              contentType: 'application/json',
              url: e._links.self.href,
              data: JSON.stringify(chatServer),
              success: function(e) {
                chatServersListVue.updateChatServersList(0);
                return UIkit.notification({
                  message: "Enabled: " + e.enabled + ".",
                  status: 'success',
                  pos: 'top-center',
                  timeout: 2000
                });
              },
              error: function(xhr, msg, ext) {
                return serverErrorHandler(xhr, msg, ext);
              }
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      left: function(event) {
        var cur;
        event.stopPropagation();
        cur = this.chatServersList.page.number;
        if (cur > 0) {
          return this.page = cur - 1;
        }
      },
      right: function(event) {
        var cur;
        event.stopPropagation();
        cur = this.chatServersList.page.number;
        if (cur < (this.chatServersList.page.totalPages - 1)) {
          return this.page = cur + 1;
        }
      },
      updateChatServersList: function(page) {
        return $.ajax({
          url: "/chatServers?page=" + page + "&size=10",
          success: function(e) {
            chatServersListVue.chatServersList = e;
            return chatServersListVue.page = e.page.number;
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      }
    },
    computed: {
      pageLeft: function() {
        var ref, ref1;
        return ((ref = this.chatServersList) != null ? (ref1 = ref.page) != null ? ref1.number : void 0 : void 0) > 0;
      },
      pageRight: function() {
        var ref, ref1, ref2, ref3;
        return ((ref = this.chatServersList) != null ? (ref1 = ref.page) != null ? ref1.number : void 0 : void 0) + 1 < ((ref2 = this.chatServersList) != null ? (ref3 = ref2.page) != null ? ref3.totalPages : void 0 : void 0);
      }
    }
  });

  botsListVue = new Vue({
    el: '#botsList',
    data: {
      botsList: {},
      bots: [],
      count: 0,
      page: 0
    },
    created: function() {
      return this.updateBotsList(0);
    },
    watch: {
      page: function(page) {
        return this.updateBotsList(page);
      },
      botsList: function(val) {
        this.bots = val._embedded.bots;
        _.each(this.bots, function(bot) {
          return bot.id = _.last(bot._links.self.href.split('/'));
        });
        return this.count = val.page.totalElements;
      }
    },
    methods: {
      edit: function(botId) {
        return botEditorVue.setup(botId);
      },
      left: function(event) {
        var cur;
        event.stopPropagation();
        cur = this.botsList.page.number;
        if (cur > 0) {
          return this.page = cur - 1;
        }
      },
      right: function(event) {
        var cur;
        event.stopPropagation();
        cur = this.botsList.page.number;
        if (cur < (this.botsList.page.totalPages - 1)) {
          return this.page = cur + 1;
        }
      },
      updateBotsList: function(page) {
        return $.ajax({
          url: "/bots?page=" + page,
          success: function(e) {
            return botsListVue.botsList = e;
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      }
    },
    computed: {
      pageLeft: function() {
        var ref;
        return ((ref = this.botsList.page) != null ? ref.number : void 0) > 0;
      },
      pageRight: function() {
        var ref, ref1;
        return ((ref = this.botsList.page) != null ? ref.number : void 0) + 1 < ((ref1 = this.botsList.page) != null ? ref1.totalPages : void 0);
      }
    }
  });

  modalNewBotVue = new Vue({
    el: '#newBotModal',
    data: {
      name: '',
      from: 'Starter',
      botsList: [],
      bots: [],
      message: ''
    },
    methods: {
      close: function() {
        this.message = '';
        this.name = '';
        return this.from = 'Starter';
      },
      updateBotsList: function() {
        return $.ajax({
          url: '/bots?size=10000',
          success: function(e) {
            return modalNewBotVue.botsList = e._embedded.bots;
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      createNewBot: function() {
        var userName;
        userName = sideMenuVue.userName;
        if (!userName) {
          this.message = 'Please set your name.';
          return;
        }
        if (!this.name) {
          this.message = 'Please set new bot name.';
          return;
        }
        return $.ajax({
          method: 'POST',
          url: '/create',
          data: {
            'name': this.name,
            'from': this.from,
            'user': userName
          },
          success: function(e) {
            modalNewBotVue.message = e;
            return $.ajax({
              url: '/bots',
              success: function(e) {
                botsListVue.botsList = e;
                return modalNewBotVue.updateBotsList();
              },
              error: function(xhr, msg, ext) {
                return serverErrorHandler(xhr, msg, ext);
              }
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      }
    }
  });

  botEditorVue = new Vue({
    el: '#botEditor',
    data: {
      bot: {},
      visible: false,
      editor: {},
      testMessage: ''
    },
    mounted: function() {
      ace.require("ace/ext/language_tools");
      this.editor = ace.edit("editor");
      this.editor.setOptions({
        enableBasicAutocompletion: true,
        enableLiveAutocompletion: true,
        showPrintMargin: false,
        fontSize: "85%"
      });
      this.editor.setTheme("ace/theme/monokai");
      return this.editor.getSession().setMode("ace/mode/groovy");
    },
    computed: {
      formatedCreatedDate: function() {
        var ref;
        return (ref = this.bot.createdDate) != null ? ref.replace('T', ' ').substring(0, 19) : void 0;
      },
      formatedUpdatedDate: function() {
        var ref;
        return (ref = this.bot.updatedDate) != null ? ref.replace('T', ' ').substring(0, 19) : void 0;
      }
    },
    methods: {
      setup: function(botId) {
        this.testMessage = '';
        $('#testResult').html('');
        return $.ajax({
          url: "/bots/" + botId,
          success: function(e) {
            var b64Decoded, decoded;
            botEditorVue.bot = e;
            botEditorVue.bot.script = e.script;
            botEditorVue.bot.createdDate = moment(e.createdDate).format();
            botEditorVue.bot.updatedDate = moment(e.updatedDate).format();
            if (botEditorVue.bot.updatedDate === 'Invalid date') {
              botEditorVue.bot.updatedDate = '';
            }
            botEditorVue.editor.$blockScrolling = 2e308;
            b64Decoded = base64js.toByteArray(e.script);
            decoded = new TextDecoderLite('utf-8').decode(b64Decoded);
            botEditorVue.editor.setValue(decoded);
            return botEditorVue.editor.gotoLine(0);
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      toggleE: function() {
        this.bot.revisedBy = sideMenuVue.userName;
        this.bot.updatedDate = moment().format();
        this.bot.enabled = !this.bot.enabled;
        return $.ajax({
          method: 'PUT',
          contentType: 'application/json',
          url: this.bot._links.self.href,
          data: JSON.stringify(this.bot),
          success: function(e) {
            botsListVue.updateBotsList(botsListVue.page);
            return UIkit.notification({
              message: "Enabled: " + e.enabled + ".",
              status: 'success',
              pos: 'top-center',
              timeout: 2000
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      toggleAA: function() {
        this.bot.revisedBy = sideMenuVue.userName;
        this.bot.updatedDate = moment().format();
        this.bot.acceptAll = !this.bot.acceptAll;
        return $.ajax({
          method: 'PUT',
          contentType: 'application/json',
          url: this.bot._links.self.href,
          data: JSON.stringify(this.bot),
          success: function(e) {
            return UIkit.notification({
              message: "Accept All: " + e.acceptAll + ".",
              status: 'success',
              pos: 'top-center',
              timeout: 2000
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      deleteBot: function() {
        if (this.bot.name === 'Starter') {
          UIkit.notification({
            message: "Can't delete 'Starter'.",
            status: 'warning',
            pos: 'top-center',
            timeout: 2000
          });
          return;
        }
        return $.ajax({
          method: 'DELETE',
          url: this.bot._links.self.href,
          success: function(e) {
            botsListVue.updateBotsList(botsListVue.page);
            botEditorVue.setup(1);
            return UIkit.notification({
              message: "Bot deleted.",
              status: 'success',
              pos: 'top-center',
              timeout: 2000
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      test: function() {
        var b64Encoded, encoded;
        $('#testResult').html('');
        encoded = new TextEncoderLite('utf-8').encode(this.editor.getValue());
        b64Encoded = base64js.fromByteArray(encoded);
        return $.ajax({
          method: 'POST',
          contentType: 'application/json',
          url: '/testBot',
          data: JSON.stringify({
            'botname': this.bot.name,
            'script': b64Encoded,
            'message': this.testMessage
          }),
          success: function(e) {
            console.log(e);
            $('#testResult').append(e);
            return UIkit.notification({
              message: 'Tested.',
              status: 'success',
              pos: 'bottom-right',
              timeout: 2000
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      },
      save: function() {
        var b64Encoded, encoded;
        this.bot.revision++;
        encoded = new TextEncoderLite('utf-8').encode(this.editor.getValue());
        b64Encoded = base64js.fromByteArray(encoded);
        this.bot.script = b64Encoded;
        this.bot.revisedBy = sideMenuVue.userName;
        this.bot.updatedDate = moment().format();
        return $.ajax({
          method: 'PUT',
          contentType: 'application/json',
          url: this.bot._links.self.href,
          data: JSON.stringify(this.bot),
          success: function(e) {
            botsListVue.updateBotsList(botsListVue.page);
            return UIkit.notification({
              message: 'Saved.',
              status: 'success',
              pos: 'bottom-right',
              timeout: 2000
            });
          },
          error: function(xhr, msg, ext) {
            return serverErrorHandler(xhr, msg, ext);
          }
        });
      }
    }
  });

  $(function() {
    $('body').removeClass('uk-invisible');
    return console.log('coffee main started.');
  });

}).call(this);
