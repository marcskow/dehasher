var newMaxForNormalization = 100;
var initialReady;

function handleClose(progressNumber) {
    deleteProgressBar(progressNumber);
}

function handleTaskUpdate(progressNumber, response) {
    var partials = response["partialRanges"];
    var range = response["wholeRange"];

    var segments = [];
    if(parseInt(partials[0]["start"]) != 0){
        segments.push(0);
        initialReady = false;
    } else {
        initialReady = true;
    }

    for(var i = 0; i < partials.length; i++){
        segments.push(parseInt(partials[i]["start"]));
        segments.push(parseInt(partials[i]["end"]));
    }

    if(parseInt(partials[partials.length - 1]["end"]) != range){
        segments.push(range);
    }

    changeProgressBar(progressNumber, fillSegments(segments, range));
}

function fillSegments(segments, range) {
    var result = [];
    var normalized = normalize(segments, range);

    var empty = initialReady;
    for (var i = 0; i < normalized.length - 1; i++) {
        if(normalized[i+1] - normalized[i] != 0) {
            result.push({
                range: rangeToPercent(normalized[i], normalized[i + 1], newMaxForNormalization),
                ready: empty
            });
        }
        empty = !empty;
    }

    return result;
}

function normalize(array, range) {
    var result = [];

    for(var i = 0; i < array.length; i++){
        result[i] = (array[i] / range) * newMaxForNormalization;
    }
    return result;
}

function rangeToPercent(from, to, max) {
    return (to - from) / max;
}

function countReady(segmentsArray) {
    var result = 0;
    for (var i = 0; i < segmentsArray.length; i++){
        if(segmentsArray[i].ready) {
            result = result + (segmentsArray[i].range * 100);
        }
    }
    return result.toFixed(2);
}