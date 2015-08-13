var cookieHelper = require('javascript/helper/cookie');

var defaultMode = 'mobile';
var desktopEnabled;

var singleViewerOrientation = 'portrait';
var activeMode;

var onUpdateFn;

function init(options) {

    if (cookieHelper.get('desktopEnabled')) {
        desktopEnabled = true;
    }

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

function isDesktopActive() {
    return desktopEnabled;
}

function enableDesktop() {
    if (desktopEnabled) {
        return;
    }

    desktopEnabled = true;
    cookieHelper.set('desktopEnabled', true);

    if (onUpdateFn) {
        onUpdateFn();
    }
}

function getMode() {
    return activeMode;
}

module.exports = {
    init: init,
    updateMode: updateMode,
    getMode: getMode,
    setSingleViewerOrientation: setSingleViewerOrientation,
    getSingleViewerOrientation: getSingleViewerOrientation,
    isDesktopActive: isDesktopActive,
    enableDesktop: enableDesktop
};
