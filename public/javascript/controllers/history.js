var viewer = require('../components/viewer');

function init() {
    //Bind to history change (for back buttons!)
    window.addEventListener('popstate', onPopState);

    document.getElementById('viewer').addEventListener('load', function(e){
        addLocationHistory(e.target.contentWindow.location.pathname);
    })
}

function onPopState(e){
    //TODO REMOVE HARDCODED PREVIEW
    viewer.updateUrl('http://preview.guardian.com/' + e.state.path)
}

function addLocationHistory(path) {
    //TODO REMOVE HARDCODED PREVIEW
    window.history.pushState({path: path}, "", "/preview" + path);
}

module.exports = {
    init: init,
    updateUrl: addLocationHistory
};
