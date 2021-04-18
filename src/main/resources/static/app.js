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
    $( "#sendRest" ).click(function() { sendRest(); });
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

function sendRest() {

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            alert(this.responseText);
        }
        console.log(this.responseText);
    };
    xhttp.open("POST", "https://ball-game-petro-yarik-vadim.herokuapp.com/configure/create", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(JSON.stringify({'players': ['Vadim', 'Petro', 'Yarik'], 'estimated': 10}));

    // var response = fetch('https://ball-game-petro-yarik-vadim.herokuapp.com/configure/create', {
    //     method: 'POST',
    //     body: JSON.stringify({'players': ['Vadim', 'Petro', 'Yarik'], 'estimated': 10}), // string or object
    //     headers: {
    //         'Content-Type': 'application/json'
    //     }
    // });

    //console.log(xhttp);
    // do something with myJson


}

var success = true;
var roundNext = false;

function Error() {
    success = false;
}

function nextRound() {
    roundNext = true;
}

function logState() {
    var player = this.getAttribute("name");
    if(!success) {
        document.getElementById("log").innerHTML += '<br/>' + 'Incorrect pass' + '<br/>';
    }
    if(roundNext == true) {
        document.getElementById("log").innerHTML += '<br/>';
    }
    document.getElementById("log").innerHTML += player + ' ';
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
            span.innerHTML = "Pass";
            btn.appendChild(span);
            x.appendChild(btn);
            //btn[i] = button.getAttribute("name");
            console.log(btn);
        }
    }
}





