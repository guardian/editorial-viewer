var pageOpenTime = Date.now();

function init() {
    const gaId = window._clientConfig.gaId;
    // tracking script should be on the page already
    if (gaId) {
        window.ga =
            window.ga ||
            (function() { return (window.ga.q = window.ga.q || []).push(arguments); });

        window.ga('create', gaId, 'auto');
        window.ga('set', 'transport', 'beacon');
        window.ga('send', 'pageview');
    }
    return function() { return window.debugGA && console.log(arguments); };
}

function trackEvent(category, action, label, value, dimensions) {
    if (window.ga) {
        window.ga('send', 'event', category, action, label, value, dimensions);
    }
}

function recordOrientationChange() {
    trackEvent('View', 'Changed', 'Orientation switched');
}

function recordDesktopEnabled() {
    if (pageOpenTime) {
        var timeTaken = Math.floor((Date.now() - pageOpenTime) / 1000);
        pageOpenTime = null;
        trackEvent('View', 'Enabled', 'Desktop enabled', null, null, {timeTakenToEnable: timeTaken});
    }

    trackEvent('View', 'Enabled', 'Desktop enabled');

}

function recordMobileViewed() {
    trackEvent('View', 'Changed', 'Mobile viewed');
}

function recordDesktopViewed() {
    trackEvent('View', 'Changed', 'Desktop viewed');
}

function recordAdsDisabled() {
    trackEvent('View', 'Changed', 'Ads Disabled');
}

function recordReaderMode() {
    trackEvent('View', 'Changed', 'Reader mode');
}

function recordPrint() {
    trackEvent('View', 'Changed', 'Print version');
}

module.exports = {
    init:                    init,
    recordOrientationChange: recordOrientationChange,
    recordDesktopEnabled:    recordDesktopEnabled,
    recordDesktopViewed:     recordDesktopViewed,
    recordMobileViewed:      recordMobileViewed,
    recordReaderMode:        recordReaderMode,
    recordAdsDisabled:       recordAdsDisabled,
    recordPrint:             recordPrint
};
