var viewer = require('../components/viewer');
var applicationController = require('./application');
var alreadyRan = false;

function onKeyPress(e) {

    if ((e.ctrlKey || e.metaKey) && (e.key === 'p' || e.keyIdentifier === 'U+0050')) {
      viewer.printViewer();
      e.preventDefault();
    }

    if ((e.ctrlKey || e.metaKey) && (e.key === 'b' || e.keyIdentifier === 'U+0042')) {
      applicationController.setMode('reader');
      e.preventDefault();
    }
}

function init() {
    if (alreadyRan) {
        console.log("Already init'd keyboardController");
        return;
    }

    document.addEventListener('keydown', onKeyPress);

    alreadyRan = true;
}

module.exports = {
    init: init
};
