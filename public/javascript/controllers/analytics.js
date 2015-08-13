var isEnabled = false;

function init() {
    if (!window.mixpanel) {
        console.log('No mixpanel detected');
    }

    isEnabled = true;
    //TODO Generate session parameter?
    //TODO Get user info?
}

function recordPageOpen() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('previewOpened');
}

function recordScrollStart() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('scrollStart');
}

function recordOrientationChange() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('orientationSwitched');
}

function recordDesktopEnabled() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('desktopEnabled');
}

function recordDesktopViewed() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('desktopViewed');
}

module.exports = {
    init:                    init,
    recordPageOpen:          recordPageOpen,
    recordScrollStart:       recordScrollStart,
    recordOrientationChange: recordOrientationChange,
    recordDesktopEnabled:    recordDesktopEnabled,
    recordDesktopViewed:     recordDesktopViewed
};
