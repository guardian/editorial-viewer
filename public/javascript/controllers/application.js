var localStorageUtil = require('javascript/utils/localStorage');
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

    activeMode = defaultMode;

    bindClicks();
    updateViews();
    checkDesktopEnabled();

}

function checkDesktopEnabled() {
    console.log("check desktop enabled for: ", window.location.href)
    localStorageUtil.getEnabledHrefs().then(function(hrefs){
        if (Array.isArray(hrefs) && hrefs.indexOf(window.location.href) !== -1) {
            desktopEnabled = true;
        } else {
            desktopEnabled = false;
        }

        if (activeMode !== 'desktop') {
            //Only remove/add button
            updateDesktopVisbility();
        } else {
            //Full update including iframe refresh
            updateViews();
        }

    });
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
        buttonUtil.removeClassFromAttributeNameAndValue('switchmode', 'desktop', 'is-hidden');
        buttonUtil.addClassToAttributeNameAndValue('hidedesktopenabled', 'true', 'is-hidden');
    } else {
        buttonUtil.removeClassFromAttributeNameAndValue('toggledesktop', 'true', 'is-checked');
        buttonUtil.addClassToAttributeNameAndValue('switchmode', 'desktop', 'is-hidden', 'none');
        buttonUtil.removeClassFromAttributeNameAndValue('hidedesktopenabled', 'true', 'is-hidden');
    }
}

function toggleDesktop() {
    if (desktopEnabled) {
        if (activeMode === 'desktop') {
            activeMode = defaultMode;
        }
        desktopEnabled = false;
        localStorageUtil.removeEnabledHref(window.location.href);
    } else {
        desktopEnabled = true;
        localStorageUtil.addEnabledHref(window.location.href);
        analyticsCtrl.recordDesktopEnabled();
    }

    updateDesktopVisbility();
}

module.exports = {
    init: init,
    checkDesktopEnabled: checkDesktopEnabled
};
