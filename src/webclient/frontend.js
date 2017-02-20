var progresses = 0;
function addNewTask() {
    document.getElementById("task-container-label").innerHTML = "";

    var tasks = parseInt(document.getElementById("progress-container").getElementsByTagName("div").length / 4);
    if(tasks > 0){
        document.getElementById("task-container-label").innerHTML = '<button id="update-all" style="float: left; width: 100px; margin-right: 30px;" onclick="updater.notifyAll();">Update All</button>' +
            '<button id="close-all" style="float: left; width: 100px" onclick="closer.notifyAll()">Close All</button><br /><br />';
    }

    updater.attach(new UpdateObserver(progresses));
    closer.attach(new CloseObserver(progresses));

    var task = document.getElementById("task-body").value;
    document.getElementById("task-body").value = "Enter new task...";
    document.getElementById("progress-container").innerHTML += '<div id="task-container' + progresses + '"></div>';
    document.getElementById("task-container" + progresses).innerHTML += ('<b>Method:</b> ' + getMethodType() + '<br /> <b>Task:</b> ');
    document.getElementById("task-container" + progresses).innerHTML += '<div id="task' + progresses + '" style="word-break: break-all;">' + task + '</div><br />';
    document.getElementById("task-container" + progresses).innerHTML += '<div class="progress" id="myProgress' + progresses + '" style="margin-bottom: 6px;"></div>';
    document.getElementById("task-container" + progresses).innerHTML += '<div id="label' + progresses + '">Your task is currently waiting...</div>';
    document.getElementById("task-container" + progresses).innerHTML += '<button id="close" style="float: right; width: 80px;" onclick="closeTask(' + progresses + ')">Close</button>';
    document.getElementById("task-container" + progresses).innerHTML += '<button id="update" style="float: right; width: 80px; margin-right: 30px;" onclick="getTaskState(' + progresses + ')">Update</button>';
    document.getElementById("task-container" + progresses).innerHTML += '<br /><br /><br />';

    var pageHeight = parseInt(document.getElementById("content").style.height);
    var newValue = pageHeight + pageHeight * 0.10;
    document.getElementById("content").style.height = newValue + "px";
    progresses++;
}

function booleanToColor(b) {
    if(b) {
        return "limegreen";
    } else {
        return "transparent";
    }
}

function changeProgressBar(n, segmentsArray) {
    document.getElementById("myProgress" + n).innerHTML = '';
    for (var i = 0; i < segmentsArray.length; i++) {
        var effect = "";
        if(segmentsArray[i].ready){
            effect = "progress-bar-striped active";
        }
        document.getElementById("myProgress" + n).innerHTML += ' <div class="progress-bar ' + effect + '"' +
            ' role="progressbar" style="width:' + segmentsArray[i].range * 100 + '%; background-color:' + booleanToColor(segmentsArray[i].ready) + '"></div>';
    }
    document.getElementById("label" + n).innerHTML = countReady(segmentsArray) + "% of work done.";
}

function deleteProgressBar(progressNumber) {
    var element = document.getElementById("task-container" + progressNumber);
    element.parentNode.removeChild(element);

    var tasks = document.getElementById("progress-container").getElementsByTagName("div").length / 4;
    if(tasks < 1){
        document.getElementById("task-container-label").innerHTML = '<p style="font-size: 14px">Currently there are no tasks created by you...</p>';
    }

    updater.detach(progressNumber);
    closer.detach(progressNumber);
}

function solutionFound(progressNumber, solution) {
    document.getElementById("label" + progressNumber).innerHTML = 'Solution found! The solution is: ' + solution;
}

function solutionDoesNotExist(progressNumber) {
    document.getElementById("label" + progressNumber).innerHTML = 'No solution to given hash';
}

var refreshIntervalId = null;
function autoupdate() {
    if(refreshIntervalId == null) {
        refreshIntervalId = setInterval(function () {
            updater.notifyAll();
        }, 5000);
        document.getElementById("autoupdate-button").className = "btn btn-success";
        document.getElementById("autoupdate-button").innerHTML = "AutoUpdate Enabled"
    } else {
        clearInterval(refreshIntervalId);
        document.getElementById("autoupdate-button").className = "btn btn-danger";
        document.getElementById("autoupdate-button").innerHTML = "AutoUpdate Disabled"
        refreshIntervalId = null;
    }
}