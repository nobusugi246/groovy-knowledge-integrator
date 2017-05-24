<!doctype html>
<html lang="en" class="no-js">

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>
      <g:layoutTitle default="gKI Chat" />
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <asset:stylesheet src="application.css" />
    <g:layoutHead/>
  </head>

  <body>
    <div class="navbar navbar-default navbar-static-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/#">
            gKI
          </a>
        </div>
        <div class="navbar-collapse collapse" aria-expanded="false" style="height: 0.8px;">
          <ul class="nav navbar-nav">
            <li><a href="/chat/index">Chat</a></li>
          </ul>
          <ul class="nav navbar-nav">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Bots <span class="caret"></span></a>
              <ul class="dropdown-menu">
                <g:each var="server" in="${gki.chat.ChatBotServer.findAll()}">
                  <li><a href="${server.uri}">${server.name}</a></li>
                </g:each>
              </ul>
            </li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <g:pageProperty name="page.nav" />
            <li><div id="userIconImage" style="width: 40px; height: 40px;"></div></li>
          </ul>
        </div>
      </div>
    </div>

    <g:layoutBody/>

    <div class="footer" role="contentinfo"></div>

    <div id="spinner" class="spinner" style="display:none;">
      <g:message code="spinner.alt" default="Loading&hellip;" />
    </div>

    <asset:javascript src="application.js" />
  </body>

</html>
