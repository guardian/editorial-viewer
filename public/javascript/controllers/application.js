var localStorageUtil = require('javascript/utils/localStorage');
var buttonUtil = require('javascript/utils/button');
var analyticsCtrl = require('javascript/controllers/analytics');
var modes = require('../modes');
var viewer = require('javascript/components/viewer');

var desktopEnabled, activeMode, adsBlocked;
var defaultMode = 'mobile-portrait';

function init(options) {

    activeMode = defaultMode;

    viewer.init();

    bindClicks();
    updateViews();
    checkDesktopEnabled();
    checkAdBlockStatus();


}

function checkDesktopEnabled() {
    localStorageUtil.getEnabledHrefs().then(function(hrefs) {
        if (Array.isArray(hrefs) && hrefs.indexOf(window.location.href) !== -1) {
            desktopEnabled = true;
        } else {
            desktopEnabled = false;
        }

        if (activeMode === 'desktop' && !desktopEnabled) {
            activeMode = defaultMode;
        }

        updateClasses();
    });
}

function checkAdBlockStatus() {
    localStorageUtil.getAdBlockStatus().then(function(enabled) {
        adsBlocked = enabled;
        updateClasses();

        if (adsBlocked) {
            viewer.enableAdBlock();
        }
    });
}

function bindClicks() {
    buttonUtil.bindClickToAttributeName('toggledesktop', toggleDesktop);
    buttonUtil.bindClickToAttributeName('toggleads', toggleAds);
    buttonUtil.bindClickToModeUpdate('switchmode', updateMode);
    buttonUtil.bindClickToAttributeName('print', viewer.printViewer);
}

function updateViews() {

    updateClasses();

    viewer.updateViewer(activeMode, modes[activeMode]);
    buttonUtil.markSelected('switchmode', activeMode);
}

function updateClasses() {
    var className = 'is-' + activeMode;

    if (desktopEnabled) {
        className += ' desktop-enabled';
    }

    if (adsBlocked) {
        className += ' ads-blocked';
    }

    document.body.className = className;
}

function triggerAnalytics(oldMode, newMode) {
    if ((oldMode !== newMode) && modes[oldMode].isMobile && modes[newMode].isMobile) {
        analyticsCtrl.recordOrientationChange();
    }

    if ((oldMode !== 'desktop') && (newMode === 'desktop')) {
        analyticsCtrl.recordDesktopViewed();
    }

    if ((oldMode !== 'reader') && (newMode === 'reader')) {
        analyticsCtrl.recordReaderMode();
    }

    if ((oldMode !== newMode) && !modes[oldMode].isMobile && modes[newMode].isMobile) {
        analyticsCtrl.recordMobileViewed();
    }
}

function updateMode(newMode) {
    var oldMode = activeMode;

    if (newMode === 'desktop' && !desktopEnabled && window._previewEnv !== 'live') {
        return;
    }

    triggerAnalytics(oldMode, newMode);

    activeMode = newMode;

    updateViews();
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

    updateClasses();
}

function toggleAds() {
    if (adsBlocked) {
        viewer.disableAdBlock();
        localStorageUtil.saveAdBlockStatus(false);
        adsBlocked = false;
    } else {
        viewer.enableAdBlock();
        localStorageUtil.saveAdBlockStatus(true);
        adsBlocked = true;
    }

    updateClasses();
}

module.exports = {
    init:                init,
    checkDesktopEnabled: checkDesktopEnabled,
    setMode:             updateMode
};
