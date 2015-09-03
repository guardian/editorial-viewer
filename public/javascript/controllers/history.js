var viewer = require('../components/viewer');

function init() {
    //Bind to history change (for back buttons!)
    window.addEventListener('popstate', onPopState);

    //Bind to viewer load event (Probably want to do this through the viewer component)
    document.getElementById('viewer').addEventListener('load', function(e){
        //Validate it is an actual page (not blank, chrome error)
        var iframeLocation = e.target.contentWindow.location;

        if (iframeLocation.origin !== "null" || iframeLocation.protocol.indexOf('http') !== -1) {
            addLocationHistory(iframeLocation.pathname);
        }
    })
}

function onPopState(e){
    //TODO REMOVE HARDCODED PREVIEW
    viewer.updateUrl('http://preview.gutools.co.uk' + e.state.path)
}

function addLocationHistory(path) {
    //TODO REMOVE HARDCODED VIEWER URL
    window.history.pushState({path: path}, "", "/preview" + path);
}

module.exports = {
    init: init,
    updateUrl: addLocationHistory
};
