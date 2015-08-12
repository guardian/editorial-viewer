var defaultMode = 'mobile';
var singleViewerOrientation = 'portrait';
var activeMode;

var onUpdateFn;

function init(options) {
    activeMode = defaultMode;

    onUpdateFn = options.onUpdate;
}

function updateMode(mode) {
    activeMode = mode;

    if (onUpdateFn) {
        onUpdateFn();
    }
}

function setSingleViewerOrientation(value) {
    singleViewerOrientation = value;

    if (onUpdateFn) {
        onUpdateFn();
    }
}

function getSingleViewerOrientation(value) {
    return singleViewerOrientation;
}

function getMode() {
    return activeMode;
}

module.exports = {
    init: init,
    updateMode: updateMode,
    getMode: getMode,
    setSingleViewerOrientation: setSingleViewerOrientation,
    getSingleViewerOrientation: getSingleViewerOrientation
};