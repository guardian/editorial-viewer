var viewer = require('../components/viewer');
var currentPath;

function init() {
    window.addEventListener('popstate', onPopState);

    document.getElementById('viewer').addEventListener('load', function(e){
        var iframeLocation = e.target.contentWindow.location;

        if (iframeLocation.origin !== "null" || iframeLocation.protocol.indexOf('http') !== -1) {
            addLocationHistory(iframeLocation);
        }
    })
}

function onPopState(e){
    //TODO REMOVE HARDCODED PREVIEW
    
    currentPath = e.state.viewerHref;
    viewer.updateUrl(e.state.viewerHref)
}

function addLocationHistory(location) {

    var path = location.pathname;
    var href = location.href;

    //Check not already there, or not first load.

    if (!currentPath || currentPath === href) {
        currentPath = href;
        return;
    }

    //TODO REMOVE HARDCODED PREVIEW URL
    window.history.pushState({viewerHref: href}, "", '/preview' + path);
    currentPath = href;
}

module.exports = {
    init: init,
    updateUrl: addLocationHistory
};
