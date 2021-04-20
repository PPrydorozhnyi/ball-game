var stompClient = null;
var sessionId = 0;
var estimate = 0;
var totalPass = 0;
var playerList = [];
var k = 0;
var totalWaste = 0;
var totalEstimate = 0;

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
        sessionId = $("#sessionId").val();
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/session/' + sessionId, function (greeting) {
            var body = JSON.parse(greeting.body)
            switch (body.type) {
                case 'INIT':
                    document.getElementById("out_players").innerHTML = '';
                    showGreeting(body.players);
                    estimate = body.estimate;
                    playerList = body.players;
                    outputPlayers();
                    createTable()
                    if(body.currentChain && body.currentChain.length !== 0) {
                        document.getElementById("log").innerHTML += 'Chains: ';
                        for(var i = 0; i < body.currentChain.length; i++) {
                            document.getElementById("log").innerHTML += body.currentChain[i] + '<br/>';
                        }
                    }
                    break;
                case 'START_ROUND':
                    if (body.success) {
                        startTimer1();
                    } else {
                        document.getElementById("log").innerHTML += '<br/>' + 'Round has not started ';
                    }
                    break;
                case 'SKIP':
                    if (body.success) {
                        document.getElementById("log").innerHTML += '<br/>' + 'Lap has skipped' + '<br/>';
                    } else {
                        document.getElementById("log").innerHTML += '<br/>' + 'No has skipped' + '<br/>';
                    }
                    break;
                case 'FINISHED':
                    //showGreeting(body.totalPasses);
                    stopTimer();
                    k += 1;
                    totalPass += body.totalPasses;
                    var waste = estimate - totalPass;
                    totalWaste += waste;
                    totalEstimate += estimate;
                    document.getElementById("points").innerHTML = 'Total Points: ' + '<b>' + totalPass + '</b>';
                    document.getElementById("waste").innerHTML = 'Total Waste: ' + '<b>' + totalWaste + '</b>';
                    document.getElementById("est").innerHTML = 'Total Estimate: ' + '<b>' + totalEstimate + '</b>';
                    document.getElementById("roundPoints").innerHTML += 'Round ' + k + ': <b>' + body.totalPasses + ' points</b>, Waste: <b>' + waste + ' points</b><br/>';

                    break;
                case 'BUTTON_PUSH':
                    if (body.success) {
                        document.getElementById("log").innerHTML += body.playersName + ' ';
                    } else {
                        document.getElementById("log").innerHTML += '<br/>' + 'Incorrect pass to ' + body.playersName + '<br/>';
                    }
                    break;
                case 'ROUND_END':
                    document.getElementById("log").innerHTML += body.playersName + '<br/>' + 'Lap End:' + body.chain[body.chain.length-1] + '<br/>';
                    break;
                case 'ERROR':
                    showGreeting(body.message)
                    document.getElementById("log").innerHTML += '<br/>' + 'Error: ' + body.message + '<br/>';
                    break;
                case 'RETROSPECTIVE':
                    startTimerP();
                    break;
                default:
                    showGreeting(body);
            }
        });
        var initRequest = {'type': 'INIT', 'sessionId': sessionId};
        send(initRequest);
    });
}

function clearLog() {
    document.getElementById("log").innerHTML = '<b>Log:</b><br/>';
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function send(request) {
    stompClient.send("/app/hello", {}, JSON.stringify(request));
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    //document.getElementById("out_players").innerHTML += arr[i] + '<br/>';
}

function outputPlayers() {
    document.getElementById("out_players").innerHTML += '<b>' + 'Players:' + '</b><br/>';
    for(var i = 0; i < playerList.length; i++) {
        if (playerList[i] !== undefined)
            document.getElementById("out_players").innerHTML += playerList[i] + '<br/>';
    }
    document.getElementById("est").innerHTML = 'Estimate: ' + '<b>' + estimate + '</b>';
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#skip" ).click(function() { skip(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#sendRest" ).click(function() { sendRest(); });
});

function skip() {
    if (sessionId) {
        var initRequest = {'type': 'SKIP', 'sessionId': sessionId};
        send(initRequest);
    }
}

function sendRest() {

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        document.getElementById("output").innerHTML += this.responseText;
    };
    xhttp.open("POST", "https://ball-game-petro-yarik-vadim.herokuapp.com/configure/create", true);
    xhttp.setRequestHeader("Content-type", "application/json");
    xhttp.send(JSON.stringify({'players': [$("#name1").val(), $("#name2").val(), $("#name3").val(),
            $("#name4").val(), $("#name5").val(), $("#name6").val(), $("#name7").val(), $("#name8").val(),
            $("#name9").val(), $("#name10").val(), $("#name11").val(), $("#name12").val()],
        'estimated': $("#estimate").val()}));
    //console.log(xhttp); https://ball-game-petro-yarik-vadim.herokuapp.com/configure/create
}

function logState() {
    var player = this.getAttribute("name");

    var passRequest = {'type': 'BUTTON_PUSH', 'sessionId': sessionId, 'playersName': player};
    send(passRequest);
}

function createTable() {
    var table = document.getElementById("myTable");

    for(var i = 0; i < playerList.length; i++) {
        if (playerList[i] !== undefined) {
            var del = document.getElementById(playerList[i]);
            if(del) {
                del.remove();
            }
            var  x = document.createElement("TD");
            x.setAttribute("id", playerList[i]);
            var text = document.createTextNode(playerList[i]);
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
            else if(table.rows[2].cells.length < 4){
                document.getElementById("NewTr").appendChild(x);
            }
            var btn = document.createElement('button');
            var span = document.createElement('span');
            btn.setAttribute('class', 'buttonTable');
            btn.setAttribute('type', 'button');
            btn.setAttribute('name', playerList[i]);
            btn.onclick = logState;
            span.innerHTML = "Pass";
            btn.appendChild(span);
            x.appendChild(btn);
        }
    }
}

function startRound() {
    var startRound = {'type': 'START_ROUND', 'sessionId': sessionId};
    send(startRound);
}

function startRetrospective() {
    var startRound = {'type': 'RETROSPECTIVE', 'sessionId': sessionId};
    send(startRound);
}





