var cookieHelper = require('javascript/helper/cookie');
var analyticsCtrl = require('javascript/controllers/analytics');

var defaultMode = 'mobile-portrait';

var modes = {
    'mobile-portrait' : {
        isMobile: true
    },
    'mobile-landscape' : {
        isMobile: true
    },
    'desktop' : {
        isMobile: false
    }
}

var desktopEnabled;

var activeMode;

var onUpdateFn;

function init(options) {

    if (cookieHelper.get('desktopEnabled')) {
        desktopEnabled = true;
    }

    activeMode = defaultMode;

    onUpdateFn = options.onUpdate;

}

function updateMode(newMode) {
    var oldMode = activeMode;

    if ((oldMode !== newMode) && modes[oldMode].isMobile && modes[newMode].isMobile) {
        analyticsCtrl.recordOrientationChange();
    }

    activeMode = newMode;

    if (onUpdateFn) {
        onUpdateFn();
    }
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
    analyticsCtrl.recordDesktopEnabled();

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
    isDesktopActive: isDesktopActive,
    enableDesktop: enableDesktop
};
