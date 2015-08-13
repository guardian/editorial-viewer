var isEnabled = false;

function init() {
    if (!window.mixpanel) {
        console.log("No mixpanel detected");
    }

    isEnabled = true;
    //Generate session parameter?
    //Get user info?
}

function recordPageOpen() {
    if(!isEnabled) {
        return;
    }

    mixpanel.track("previewOpened");
}

function recordScrollStart() {
    if(!isEnabled) {
        return;
    }

    mixpanel.track("scrollStart");
}

function recordOrientationChange() {
    if(!isEnabled) {
        return;
    }

    mixpanel.track("orientationSwitched")
}

function recordDesktopEnabled() {
    if(!isEnabled) {
        return;
    }

    mixpanel.track("desktopEnabled")
}

function recordDesktopViewed() {
    if(!isEnabled) {
        return;
    }

    mixpanel.track("desktopViewed")
}

module.exports = {
    init: init,
    recordPageOpen: recordPageOpen,
    recordScrollStart: recordScrollStart,
    recordOrientationChange: recordOrientationChange,
    recordDesktopEnabled: recordDesktopEnabled,
    recordDesktopViewed: recordDesktopViewed
}
