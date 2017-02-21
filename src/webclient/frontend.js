function setAddress() {
    var addressInput = document.getElementById("addressInput").value;
    var matches = true; // /[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}/.test(addressInput);
    if(matches) {
        document.getElementById("addressInputContainer").className = "form-group has-success has-feedback";
        document.getElementById("addressInputIcon").className = "glyphicon glyphicon-ok form-control-feedback";
        address = addressInput;
    } else {
        document.getElementById("addressInputContainer").className = "form-group has-error has-feedback";
        document.getElementById("addressInputIcon").className = "glyphicon glyphicon-remove form-control-feedback";
    }
}

function setPort() {
    var portInput = document.getElementById("portInput").value;
    var matches = /[0-9]{1,5}/.test(portInput);
    if(matches) {
        document.getElementById("portInputContainer").className = "form-group has-success has-feedback";
        document.getElementById("portInputIcon").className = "glyphicon glyphicon-ok form-control-feedback";
        port = portInput;
    } else {
        document.getElementById("portInputContainer").className = "form-group has-error has-feedback";
        document.getElementById("portInputIcon").className = "glyphicon glyphicon-remove form-control-feedback";
    }
}

var progresses = 0;
function addNewTask() {
    addNewTaskDefined(getMethodType(), document.getElementById("task-body").value, getRangeLimit());
}

function addNewTaskDefined(methodType, hash, range) {
    document.getElementById("task-container-label").innerHTML = "";

    var tasks = parseInt(document.getElementById("progress-container").getElementsByTagName("div").length / 4);
    if(tasks > 0){
        document.getElementById("task-container-label").innerHTML = '<button id="update-all" style="float: left; width: 100px; margin-right: 30px;" onclick="updater.notifyAll();">Update All</button>' +
            '<button id="close-all" style="float: left; width: 100px" onclick="closer.notifyAll()">Close All</button><br /><br />';
    }

    updater.attach(new UpdateObserver(progresses));
    closer.attach(new CloseObserver(progresses));

    document.getElementById("task-body").value = "Enter new task...";
    document.getElementById("progress-container").innerHTML += '<div id="task-container' + progresses + '"></div>';
    document.getElementById("task-container" + progresses).innerHTML += ('<b>Method:</b> ' + methodType + '  <b style="margin-left: 3px; margin-right: 3px;">Length limit:</b> ' + range + '<br /> <b>Task:</b> ');
    document.getElementById("task-container" + progresses).innerHTML += '<div id="task' + progresses + '" style="word-break: break-all;">' + hash + '</div><br />';
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