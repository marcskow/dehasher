var updater = new Subject();
var closer = new Subject();

function Subject() {
    this.observers = [];
    this.attach = function (o) {
        this.observers.push(o);
    };
    this.detach = function (o) {
        var index = this.observers.indexOf(o);
        this.observers.splice(index, 1);
    };
    this.notifyAll = function () {
        this.observers.forEach(function (o) {
           o.notify();
        });
    };
}

function UpdateObserver(id) {
    this.id = id;
}

UpdateObserver.prototype.notify = function () {
    getTaskState(this.id);
};

function CloseObserver(id) {
    this.id = id;
}

CloseObserver.prototype.notify = function () {
    closeTask(this.id);
};