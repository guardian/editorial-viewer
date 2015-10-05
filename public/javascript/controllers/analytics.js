var isEnabled = false;

var pageOpenTime = Date.now();

// function guid() {
//   function s4() {
//     return Math.floor((1 + Math.random()) * 0x10000)
//       .toString(16)
//       .substring(1);
//   }
//   return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
//     s4() + '-' + s4() + s4() + s4();
// }


function init() {
    if (!window.mixpanel) {
        console.log('No mixpanel detected');
    }

    isEnabled = true;

    //var unique = guid()
    //mixpanel.identify(unique);
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

    if (pageOpenTime) {
        var timeTaken = Math.floor((Date.now() - pageOpenTime) / 1000)
        pageOpenTime = null;
        window.mixpanel.people.set({"timeTakenToEnable": timeTaken})
    }

    window.mixpanel.track('desktopEnabled');

}

function recordMobileViewed() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('mobileViewed');
}

function recordDesktopViewed() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('desktopViewed');
}


function recordAdsDisabled() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('adsDisabled');
}

function recordReaderMode() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('readerModeViewed');
}

function recordPrint() {
    if (!isEnabled) {
        return;
    }

    window.mixpanel.track('printTriggered');
}

module.exports = {
    init:                    init,
    recordPageOpen:          recordPageOpen,
    recordScrollStart:       recordScrollStart,
    recordOrientationChange: recordOrientationChange,
    recordDesktopEnabled:    recordDesktopEnabled,
    recordDesktopViewed:     recordDesktopViewed,
    recordMobileViewed:      recordMobileViewed,
    recordReaderMode:        recordReaderMode,
    recordAdsDisabled:       recordAdsDisabled,
    recordPrint:             recordPrint
};
