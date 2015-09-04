var viewer = require('../components/viewer');

var initialHref;

function init() {
    window.addEventListener('popstate', onPopState);

    document.getElementById('viewer').addEventListener('load', function(e){
        var iframeLocation = e.target.contentWindow.location;

        if (iframeLocation.origin !== "null" || iframeLocation.protocol.indexOf('http') !== -1) {
            //Needs to be replace (not push) as the iframe has added it's own history entry (shakes fist).
            replaceLocationHistory(iframeLocation);
        }
    })
}

function onPopState(e){
    if (e.state && e.state.viewerHref) {
        viewer.updateUrl(e.state.viewerHref)
    } else if (initialHref) {
        viewer.updateUrl(initialHref)
    }
}

function replaceLocationHistory(location) {

    if (!initialHref) {
        initialHref = location.href;
    }

    var newPath = window._baseAppUrl + location.pathname;
    var viewerHref = location.href;

    window.history.replaceState({viewerHref: viewerHref}, "", newPath);
}

module.exports = {
    init: init,
    updateUrl: replaceLocationHistory
};
