import localStorageUtil from '../utils/localStorage';
import buttonUtil from '../utils/button';
import { modes } from '../modes';
import type { Mode } from '../modes';
import viewer from '../components/viewer';
import error from './error';
import api from '../utils/api';
import overlay from './overlay';

let desktopEnabled = false
const defaultMode = 'mobile-portrait';
let activeMode: Mode = defaultMode;
let adsBlocked = false;

function init() {
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
        if (adsBlockedDisabledUntil && Date.now() < +adsBlockedDisabledUntil) {
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
    buttonUtil.bindClickToModeUpdate('switchmode', setMode);
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

function setMode(newMode: Mode) {
    if (newMode === 'desktop' && !desktopEnabled && window._previewEnv !== 'live') {
        return;
    }

    activeMode = newMode;

    updateViews();
}

function redirectToPreview() {
    window.location.href = `https://preview.gutools.co.uk/${window.location.href.split('/preview/')[1]}#noads`;
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
    }

    updateClasses();
}

async function appPreview() {
    try {
        const request = api.appPreviewRequest();
        const response = await request;
        if ([401, 419].includes(response.status)) {
            error.showError('You are not authorised, try logging into composer.');
        }
    } catch {
        error.showError('Error while sending preview email.');
    }

    overlay.showOverlay();
}

export default {
    init,
    checkDesktopEnabled,
    setMode,
};
