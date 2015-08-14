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
    if (cookieUtil.get('desktopEnabled') === true) {
        desktopEnabled = true;
    }

    activeMode = defaultMode;

    bindClicks();
    updateViews();
}

function bindClicks() {
    buttonUtil.bindClickToAttributeName('toggledesktop', toggleDesktop);
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

    if ((oldMode !== newMode) && modes[oldMode].isMobile && !modes[newMode].isMobile) {
        analyticsCtrl.recordDesktopViewed();
    }

    if ((oldMode !== newMode) && !modes[oldMode].isMobile && modes[newMode].isMobile) {
        analyticsCtrl.recordMobileViewed();
    }

    activeMode = newMode;

    updateViews();
}

function updateDesktopVisbility() {
    if (desktopEnabled) {
        buttonUtil.addClassToAttributeNameAndValue('toggledesktop', 'true', 'is-checked');
        buttonUtil.styleWithAttributeNameAndValue('switchmode', 'desktop', 'display', 'inline-block');
        buttonUtil.styleWithAttributeNameAndValue('switchmode', 'desktop', 'opacity', '1');

    } else {
        buttonUtil.removeClassFromAttributeNameAndValue('toggledesktop', 'true', 'is-checked');
        buttonUtil.styleWithAttributeNameAndValue('switchmode', 'desktop', 'display', 'none');
        buttonUtil.styleWithAttributeNameAndValue('switchmode', 'desktop', 'opacity', '0');

    }
}

function toggleDesktop() {
    if (desktopEnabled) {
        desktopEnabled = false;
        cookieUtil.set('desktopEnabled', false);
    } else {
        desktopEnabled = true;
        cookieUtil.set('desktopEnabled', true);
        analyticsCtrl.recordDesktopEnabled();
    }

    updateViews();
}

module.exports = {
    init: init
};
