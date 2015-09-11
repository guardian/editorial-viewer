var viewer = require('../components/viewer');
var applicationController = require('./application');
var alreadyRan = false;

function onKeyPress(e) {

    if ((e.ctrlKey || e.metaKey) && (e.key === 'p' || e.keyIdentifier === 'U+0050')) {
      viewer.printViewer();
      e.preventDefault();
    }

    if (e.key === 'ArrowUp' || e.keyIdentifier === "Up") {
      viewer.scrollViewerUp();
      e.preventDefault();
    }

    if (e.key === 'ArrowDown' || e.keyIdentifier === "Down") {
      viewer.scrollViewerDown();
      e.preventDefault();
    }

    if (e.key === '1' || e.keyIdentifier === 'U+0031') {
        applicationController.setMode('mobile-portrait');
        e.preventDefault();
    }

    if (e.key === '2' || e.keyIdentifier === 'U+0032') {
        applicationController.setMode('mobile-landscape');
        e.preventDefault();
    }

    if (e.key === '3' || e.keyIdentifier === 'U+0033') {
        applicationController.setMode('reader');
        e.preventDefault();
    }

    if (e.key === '4' || e.keyIdentifier === 'U+0034') {
        applicationController.setMode('desktop');
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
