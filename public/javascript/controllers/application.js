var localStorageUtil = require('../utils/localStorage');
var buttonUtil = require('../utils/button');
var analyticsCtrl = require('../controllers/analytics');
var modes = require('../modes');
var viewer = require('../components/viewer');
var error = require('./error')
var api = require('../utils/api')
var overlay = require('./overlay')

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
    localStorageUtil.getAdBlockStatus().then(function(adsBlockedDisabledUntil) {
        if (adsBlockedDisabledUntil && Date.now() < adsBlockedDisabledUntil) {
            adsBlocked = false;
            viewer.disableAdBlock();
        } else {
            adsBlocked = true;
        }

        updateClasses();
    });
}

function bindClicks() {
    buttonUtil.bindClickToAttributeName('toggledesktop', toggleDesktop);
    buttonUtil.bindClickToAttributeName('toggleads', toggleAds);
    buttonUtil.bindClickToModeUpdate('switchmode', updateMode);
    buttonUtil.bindClickToAttributeName('redirect-preview', redirectToPreview);
    buttonUtil.bindClickToAttributeName('print', viewer.printViewer);
    buttonUtil.bindClickToAttributeName('app-preview', appPreview);
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

function redirectToPreview() {
    window.location="https://preview.gutools.co.uk/" + window.location.href.split('/preview/')[1] + "#noads"
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
        var tenHoursFromNow = Date.now() + (1000 * 60 * 60 * 10);
        localStorageUtil.saveAdBlockDisabledUntil(tenHoursFromNow);


        adsBlocked = false;
        viewer.disableAdBlock();

    } else {
        viewer.enableAdBlock();
        localStorageUtil.saveAdBlockDisabledUntil(false);
        adsBlocked = true;
        analyticsCtrl.recordAdsDisabled();
    }

    updateClasses();
}

function appPreview() {
    api.appPreviewRequest()
    .then(overlay.showOverlay.bind(null))
    .fail(function (err, msg) {
      if (err.status === 419 || err.status === 401) {
        error.showError('You are not authorised, try logging into composer.');
      } else {
        error.showError('Error while sending preview email.');
      }
    });
}

module.exports = {
    init:                init,
    checkDesktopEnabled: checkDesktopEnabled,
    setMode:             updateMode
};
