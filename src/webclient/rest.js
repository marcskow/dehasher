var tasksIds = {};

function getMethodType() {
    var dropdown = document.getElementById("method-dropdown");
    return dropdown.options[dropdown.selectedIndex].text;
}

function sendTaskAsynchronous() {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://demo0237352.mockable.io/task", true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

    var taskBody = document.getElementById("task-body").value;
    var taskData = {
        hash: taskBody,
        algoType: getMethodType()
    };

    xhr.onreadystatechange = function processRequest() {
        if ((this.readyState == 4) && (this.status == 200)) {
            var response = JSON.parse(this.responseText);
            if(response == null || response == ''){
                alert("Sending task failed");
            } else {
                tasksIds[taskBody] = response["id"];
                addNewTask();
                alert('Task id: ' + response["id"]);
            }
        }
    };

    xhr.send(JSON.stringify(taskData));
}

//192.168.0.192
function getTaskIdByNumber(progressNumber) {
    var task = document.getElementById("task" + progressNumber).innerHTML;
    return tasksIds[task];
}
//http://localhost:8080/update/
function getTaskState(progressNumber) {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "http://demo0237352.mockable.io/update/" + getTaskIdByNumber(progressNumber), true);

    xhr.onreadystatechange = function processRequest() {
        if ((this.readyState == 4) && (this.status == 200)) {
            var response = this.responseText;
            if(response == null || response == ''){
                alert("Updating task info failed");
            } else {
                alert(response);
                handleTaskUpdate(progressNumber, JSON.parse(response));
            }
        }
    };

    xhr.send();
}

function closeTask(progressNumber) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://demo0237352.mockable.io/close/" + getTaskIdByNumber(progressNumber), true);

    xhr.onreadystatechange = function processRequest() {
        if ((this.readyState == 4) && (this.status == 200)) {
            var response = this.responseText;
            if(response == null || response == ''){
                alert("Closing task failed");
            } else {
                alert(response);
                handleClose(progressNumber);
            }
        }
    };

    xhr.send();
}