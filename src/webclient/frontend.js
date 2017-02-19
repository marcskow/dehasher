var progresses = 0;
function addNewTask() {
    document.getElementById("task-container-label").innerHTML = "";

    var task = document.getElementById("task-body").value;
    document.getElementById("task-body").value = "Enter new task...";
    document.getElementById("progress-container").innerHTML += '<div id="task-container' + progresses + '"></div>';
    document.getElementById("task-container" + progresses).innerHTML += ('<b>Method:</b> ' + getMethodType() + '<br /> <b>Task:</b> ');
    document.getElementById("task-container" + progresses).innerHTML += '<div id="task' + progresses + '" style="word-break: break-all;">' + task + '</div><br />';
    document.getElementById("task-container" + progresses).innerHTML += '<div class="progress" id="myProgress' + progresses + '" style="margin-bottom: 6px;"></div>';
    document.getElementById("task-container" + progresses).innerHTML += '<div id="label' + progresses + '">Your task is currently waiting...</div>';
    document.getElementById("task-container" + progresses).innerHTML += '<button id="update" style="float: right; width: 80px;" onclick="closeTask(' + progresses + ')">Close</button>';
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
    document.getElementById("task-container" + progressNumber).innerHTML = '';
}