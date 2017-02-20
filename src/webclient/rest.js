var tasksIds = {};

function getMethodType() {
    var dropdown = document.getElementById("method-dropdown");
    return dropdown.options[dropdown.selectedIndex].text;
}

function sendTaskAsynchronous() {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "http://192.168.0.192:9000/task", true);
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
    xhr.open("GET", "http://192.168.0.192:9000/update/" + getTaskIdByNumber(progressNumber), true);

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
    xhr.open("POST", "http://192.168.0.192:9000/close/" + getTaskIdByNumber(progressNumber), true);

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

    xhr.send();
}