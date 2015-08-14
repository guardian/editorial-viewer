var cookieUtil = require('javascript/utils/cookie');
var buttonUtil = require('javascript/utils/button');

var analyticsCtrl = require('javascript/controllers/analytics');

var viewer = require('javascript/components/viewer');

var desktopEnabled, activeMode;

var defaultMode = 'mobile-portrait';
var modes = {
    'mobile-portrait': {
        isMobile: true,
        width:    '330px',
        height:   '568px'
    },
    'mobile-landscape': {
        isMobile: true,
        width:    '568px',
        height:   '320px'
    },
    'desktop': {
        isMobile: false,
        width:    '',
        height:   ''
    }
};

function init(options) {
    if (cookieUtil.get('desktopEnabled')) {
        desktopEnabled = true;
    }

    activeMode = defaultMode;

    bindClicks();
    updateViews();
}

function bindClicks() {
    buttonUtil.bindClickToAttributeName('enabledesktop', enableDesktop);
    buttonUtil.bindClickToModeUpdate('switchmode', updateMode);
}

function updateViews() {
    updateDesktopVisbility();
    viewer.updateViewer(activeMode, modes[activeMode]);
    buttonUtil.markSelected('switchmode', activeMode);
}

function updateMode(newMode) {
    var oldMode = activeMode;

    if ((oldMode !== newMode) && modes[oldMode].isMobile && modes[newMode].isMobile) {
        analyticsCtrl.recordOrientationChange();
    }

    activeMode = newMode;

    updateViews();
}

function updateDesktopVisbility() {
    if (desktopEnabled) {
        buttonUtil.styleWithAttributeNameAndValue('switchmode', 'desktop', 'display', 'inline-block');
        buttonUtil.styleWithAttributeNameAndValue('enabledesktop', 'true', 'display', 'none');

    } else {
        buttonUtil.styleWithAttributeNameAndValue('switchmode', 'desktop', 'display', 'none');
        buttonUtil.styleWithAttributeNameAndValue('enabledesktop', 'true', 'display', 'inline-block');
    }
}

function enableDesktop() {
    if (desktopEnabled) {
        return;
    }
    desktopEnabled = true;
    cookieUtil.set('desktopEnabled', true);
    analyticsCtrl.recordDesktopEnabled();

    updateViews();
}

module.exports = {
    init: init
};
