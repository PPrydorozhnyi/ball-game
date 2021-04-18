var stompClient = null;
var i = 0;
var arr = ['Vadim', 'Lexa', 'Petro', 'Yarik', 'Denis'];

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}


function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    //document.getElementById("out_players").innerHTML += arr[i] + '<br/>';
    for(var i = 0; i < arr.length; i++) {
        if (arr[i] !== undefined)
            document.getElementById("out_players").innerHTML += arr[i] + '<br/>';
    }
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

/*function myFunction() {
    var table = document.getElementById("myTable");
    var  x = document.createElement("TD");
    var j = table.rows[0].cells.length;
    console.log(j);
    var text = document.createTextNode(arr[j]);
    x.appendChild(text);
    if(table.rows[2].cells.length == 4) {
        alert("Max number of the game");
        return;
    }
    else if(table.rows[0].cells.length < 4) {
        document.getElementById("myTr").appendChild(x);
        x.innerHTML += '<button id="playerPass" class="btn btn-primary btn-xs my-xs-btn" type="button" onClick="logState()" >'
            + '<span></span>Pass</button>';
    }
    else if(table.rows[1].cells.length < 4){
        document.getElementById("myNewTr").appendChild(x);
        x.innerHTML += '<button id="playerPass" class="btn btn-primary btn-xs my-xs-btn" type="button" onClick="logState()" >'
            + '<span></span>Pass</button>';
    }
    else {
        document.getElementById("NewTr").appendChild(x);
        x.innerHTML += '<button id="playerPass" class="btn btn-primary btn-xs my-xs-btn" type="button" onClick="logState()" >'
            + '<span></span>Pass</button>';
    }
}*/

function logState() {
    var player = this.getAttribute("name");
    document.getElementById("log").innerHTML += player;
    var success = true;
    var roundNext = false;
    if() {
        document.getElementById("log").innerHTML += '<br/>';
    }
}
function createTable() {
    var table = document.getElementById("myTable");
    for(var i = 0; i < arr.length; i++) {
        if (arr[i] !== undefined) {
            var  x = document.createElement("TD");
            var j = table.rows[0].cells.length;
            var text = document.createTextNode(arr[j]);
            x.appendChild(text);
            if(table.rows[2].cells.length == 4) {
                alert("Max number of the game");
                return;
            }
            else if(table.rows[0].cells.length < 4) {
                document.getElementById("myTr").appendChild(x);
            }
            else if(table.rows[1].cells.length < 4){
                document.getElementById("myNewTr").appendChild(x);
            }
            else {
                document.getElementById("NewTr").appendChild(x);
                /*x.innerHTML += '<button id="playerPass" class="btn btn-primary btn-xs my-xs-btn" type="button" onClick="logState()" >'
                    + '<span></span>Pass</button>';*/
            }
            var btn = document.createElement('button');
            var span = document.createElement('span');
            btn.setAttribute('class', 'buttonTable');
            btn.setAttribute('type', 'button');
            btn.setAttribute('name', arr[i]);
            btn.onclick = logState;
            span.innerHTML = " Pass";
            btn.appendChild(span);
            x.appendChild(btn);
            //btn[i] = button.getAttribute("name");
            console.log(btn);
        }
    }
}





