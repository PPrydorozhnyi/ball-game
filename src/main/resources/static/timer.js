var timer;
var timerP;

function startTimer() {
    var my_timer = document.getElementById("my_timer");
    var time = my_timer.innerHTML;
    var arr = time.split(":");
    var m = arr[0];
    var s = arr[1];
    if (s == 0) {
        if (m == 0) {
            document.getElementById("my_timer").innerHTML = "03:00";
            alert("Time is up")
            return;
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
    document.getElementById("my_timer").innerHTML = "03:00";
}

function stopTimerP() {
    clearInterval(timerP);
    document.getElementById("my_Newtimer").innerHTML = "01:00";
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
            alert("Time is up")
            return;
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