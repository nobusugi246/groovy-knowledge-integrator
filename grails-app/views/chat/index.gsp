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
              <g:submitButton name="upload" value="upload" />
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
            Users in this Chat Room
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
            </div>
          </form>
        </div>
        <hr/>
        <div class="row">
          <div id="area00" style="height: 800px; overflow: scroll;">
          </div>
        </div>
      </div>
    </div>
    
  </body>
</html>
