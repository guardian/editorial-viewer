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
    viewer.updateUrl(e.state.viewerHref)
}

function addLocationHistory(location) {

    //TODO REMOVE HARDCODED PREVIEW URL
    var newPath = '/preview' + location.pathname;
    var viewerHref = location.href;

    if (newPath !== window.location.pathname) {
        window.history.pushState({viewerHref: viewerHref}, "", newPath);
    }

}

module.exports = {
    init: init,
    updateUrl: addLocationHistory
};
