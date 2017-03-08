var tasksIds = {};
var taskIdsHash = {};

//192.168.0.192
var address = "192.168.0.192";
var port = "9000";
function buildAddress() {
    return "http://" + address + ":" + port;
}

function getMethodType() {
    var dropdown = document.getElementById("method-dropdown");
    return dropdown.options[dropdown.selectedIndex].text;
}

function getRangeLimit() {
    return document.getElementById("chars-limit").value
}

function createTaskValidation() {
    var result = true;
    if(getRangeLimit() == "" || getRangeLimit() == null){
        alert('Range limit can not be null');
        result = false;
    }
    if(document.getElementById("task-body").value == "Enter new task..."){
        alert('Did you forget to provide task body?');
        result = false;
    }
    return result;
}

function sendTaskAsynchronous() {
    if(!createTaskValidation()){
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", buildAddress() + "/task", true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

    var taskBody = document.getElementById("task-body").value;
    var taskData = {
        hash: taskBody,
        algoType: getMethodType(),
        range: parseInt(getRangeLimit())
    };

    xhr.onreadystatechange = function processRequest() {
        if ((this.readyState == 4) && (this.status == 200)) {
            var response = JSON.parse(this.responseText);
            if(response == null || response == ''){
                console.log("Sending task failed");
            } else {
                tasksIds[taskBody] = response["id"];
                addNewTask();
                console.log('Task id: ' + response["id"]);
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
    xhr.open("GET", buildAddress() + "/update/" + getTaskIdByNumber(progressNumber), true);

    xhr.onreadystatechange = function processRequest() {
        if ((this.readyState == 4) && (this.status == 200)) {
            var response = this.responseText;
            if(response == null || response == ''){
                console.log("Updating task info failed");
            } else {
                console.log(response);
                handleTaskUpdate(progressNumber, JSON.parse(response));
            }
        }
    };

    xhr.send();
}

function closeTask(progressNumber) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", buildAddress() + "/cancel", true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

    var taskData = {
        id: getTaskIdByNumber(progressNumber)
    };

    xhr.onreadystatechange = function processRequest() {
        if ((this.readyState == 4) && (this.status == 200)) {
            var response = this.responseText;
            if(response == null || response == ''){
                console.log("Closing task failed");
            } else {
                console.log(response);
                handleClose(progressNumber, JSON.parse(response));
            }
        }
    };

    xhr.send(JSON.stringify(taskData));
}

function listAllTasks() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", buildAddress() + "/task", true);

    xhr.onreadystatechange = function processRequest() {
        if ((this.readyState == 4) && (this.status == 200)) {
            var response = this.responseText;
            if(response == null || response == ''){
                console.log("Getting tasks list failed");
            } else {
                console.log(response);
                handleTaskListing(JSON.parse(response));
            }
        }
    };

    xhr.send();
}