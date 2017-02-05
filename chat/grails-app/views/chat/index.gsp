<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main" />
  </head>

  <body>
    <div class="row">
      <!-- Sidebar -->
      <div class="col-sm-3" style="background-color: #f0f0f0;">
        <!-- -->
        <div id="datetimepickerInline"></div>

        <!-- -->
        <div class="row">
          <div class="col-sm-5">
            <span class="glyphicon glyphicon-time"></span>
            Time
          </div>
          <div class="col-sm-7" id="currentTime" style="text-align: center;">
            00/00 00:00:00
          </div>
        </div>
        <div class="row">&nbsp;</div>

        <!-- -->
        <div class="row">
          <div class="col-sm-5">
            <button type="button" class="btn btn-default btn-xs"
                    id="iconImageUploadPopover" data-toggle="collapse"
                    data-target="#fileUploadCollapse" aria-expanded="false"
                    aria-controls="fileUploadCollapse"
                    >
              <span class="glyphicon glyphicon-user"></span>
            </button>
            Your Name
          </div>
          <div class="col-sm-7">
            <input type="text" id="userName" class="form-control" 
                   placeholder="Set your name here..." required>
            </input>
          </div>
        </div>

        <!-- -->
        <div class="collapse" id="fileUploadCollapse">
          <div class="well well-sm">
            <span class="glyphicon glyphicon-file"></span>
            Icon Image File Upload
            <g:uploadForm controller="chat" action="uploadFile">
              <input type="file" name="uploadFile" style="width:100%;"/>
              <g:submitButton class="btn btn-info"
                              name="upload" value="" id="uploadButton" />
            </g:uploadForm>
          </div>
        </div>

        <!-- -->
        <div class="row">
          <div class="col-sm-12">
            <span class="glyphicon glyphicon-pencil"></span>
            Chat Message
          </div>
        </div>

        <!-- -->
        <div class="row">
          <div class="col-sm-12">
            <input type="text" id="chatMessage" class="form-control"
                   data-toggle="popover" data-placement="bottom"
                   data-html="true"
                   data-content="<div id='temporaryInputPopover'>入力途中表示...</div>"
                   placeholder="" autofocus>
          </div>
        </div>
        <div class="row">&nbsp;</div>
        
        <!-- -->
        <div class="row">
          <div class="col-sm-12">
            <span class="glyphicon glyphicon-th-list"></span>
            Users List
          </div>
        </div>

        <!-- -->
        <div class="row" id="connectedUsersTable"
             style="height: 330px; overflow: scroll;">
          <table class="table table-striped">
          </table>
        </div>
      </div>

      <!-- Chat Area -->
      <div class="col-sm-9">
        <div class="row">
          <form class="form-inline">
            <div class="form-group">
              <label for="chatRoomSelected">Chat Room</label>
              <select class="form-control" id="chatRoomSelected">
                <g:each in="${gki.chat.ChatRoom.list()}" var="room">
                  <option value="${room.id}">${room.name}</option>
                </g:each>
              </select>
              &nbsp;
              <span id="wsstatus" class="label label-danger">OffLine</span>
              &nbsp;
              <a class="btn btn-warning btn-xs" id="refresh"
                 href="javascript: window.location.reload(true);"
                 data-toggle="tooltip" data-placement="top"
                 title="Refresh the Chat">
                <span class="glyphicon glyphicon-refresh"></span>
              </a>
              &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
              <a class="btn btn-warning btn-xs" id="usage"
                 data-toggle="tooltip" data-placement="top"
                 title="Usage">
                <span class="glyphicon glyphicon-question-sign"></span>
              </a>
            </div>
          </form>
        </div>

        <div> <!-- class="row" -->
          <ul class="nav nav-tabs" role="tablist">
            <li role="presentation" class="active">
              <a href="#tabNow" aria-controls="tabNow"
                 role="tab" data-toggle="tab" id="AncTabNow">
                Now
                &nbsp;
                <span id="nowNumberBadge" class="label label-info">0</span>
                &nbsp;
                <button class="btn btn-primary btn-xs" id="now-fast-backward"
                        type="submit">
                  <span class="glyphicon glyphicon-fast-backward"></span>
                </button>
                &nbsp;
                <button class="btn btn-primary btn-xs" id="now-fast-forward"
                        type="submit">
                  <span class="glyphicon glyphicon-fast-forward"></span>
                </button>
              </a>
            </li>
            <li role="presentation">
              <a href="#tabPast" aria-controls="tabPast"
                 role="tab" data-toggle="tab" id="AncTabPast">
                Past
                &nbsp;
                <span id="logNumberBadge" class="label label-info">0</span>
                &nbsp;
                <button class="btn btn-primary btn-xs" id="log-fast-backward"
                        type="submit">
                  <span class="glyphicon glyphicon-fast-backward"></span>
                </button>
                &nbsp;
                <button class="btn btn-primary btn-xs" id="log-fast-forward"
                        type="submit">
                  <span class="glyphicon glyphicon-fast-forward"></span>
                </button>
              </a>
            </li>
          </ul>

          <div class="tab-content" style="height:500px; overflow: scroll; overflow-x:hidden;" id="tabContent">
            <div role="tabpanel" class="tab-pane" id="tabPast">
              <div id="area_log"></div>
            </div>
            <div role="tabpanel" class="tab-pane active" id="tabNow">
              <div id="area_now"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
  </body>
</html>
