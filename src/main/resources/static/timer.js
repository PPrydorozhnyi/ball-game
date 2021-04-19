var timer;
var timerP;
var timeLine = "";

function startTimer1() {
    if(playerList.length < 6) {
        console.log("Less than 6, ");
        console.log(playerList.length);
        timeLine = "01:00";
        document.getElementById("my_timer").innerHTML = "01:00";
    }
    else {
        console.log("More than 6, ");
        console.log(playerList.length);
        timeLine = "03:00";
        document.getElementById("my_timer").innerHTML = "03:00";
    }
    startTimer();
}

function startTimer() {
    var my_timer = document.getElementById("my_timer");
    var time = my_timer.innerHTML;
    var arr = time.split(":");
    var m = arr[0];
    var s = arr[1];
    if (s == 0) {
        if (m == 0) {
            document.getElementById("my_timer").innerHTML = timeLine;
            alert("Time is up")
            document.getElementById("log").innerHTML += '<br/>' + "Round has finished" + '<br/>';
            return;
        }
        if(m == 3 && timeLine == "03:00" || m == 1 && timeLine == "01:00" ) {
            document.getElementById("log").innerHTML += "Round has started" + '<br/>';
        }
        m--;
        if (m < 10) m = "0" + m;
        s = 59;
    }
    else s--;
    if (s < 10) s = "0" + s;
    document.getElementById("my_timer").innerHTML = m+":"+s;
    timer = setTimeout(startTimer, 1000);
}

function stopTimer() {
    clearInterval(timer);
    if(playerList.length < 6) {
        document.getElementById("my_timer").innerHTML = "01:00";
    }
    else {
        document.getElementById("my_timer").innerHTML = "03:00";
    }

    document.getElementById("log").innerHTML += '<br/>' + "Round has stopped" + '<br/>';
}

function stopTimerP() {
    clearInterval(timerP);
    document.getElementById("my_Newtimer").innerHTML = "01:00";
    document.getElementById("log").innerHTML += '<br/>' + "Retrospective has stopped" + '<br/>';
}

function startTimerP() {
    var my_newtimer = document.getElementById("my_Newtimer");
    var timeP = my_newtimer.innerHTML;
    var arr = timeP.split(":");
    var m = arr[0];
    var s = arr[1];
    if (s == 0) {
        if (m == 0) {
            document.getElementById("my_Newtimer").innerHTML = "01:00";
            alert("Time is up");
            document.getElementById("log").innerHTML += '<br/>' + "Retrospective has finished" + '<br/>';
            return;
        }
        if(m == 1) {
            document.getElementById("log").innerHTML += "Retrospective has started" + '<br/>';
        }
        m--;
        if (m < 10) m = "0" + m;
        s = 59;
    }
    else s--;
    if (s < 10) s = "0" + s;
    document.getElementById("my_Newtimer").innerHTML = m+":"+s;
    timerP = setTimeout(startTimerP, 1000);
}