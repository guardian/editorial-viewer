var viewer = require('../components/viewer');
var alreadyRan = false;

function onKeyPress(e) {

    if ((e.ctrlKey || e.metaKey) && (e.key === 'p' || e.keyIdentifier === "U+0050")) {
      viewer.printViewer();
      e.preventDefault();
    }
}


function init() {
    if (alreadyRan) {
        console.log("Already init'd keyboardController")
        return;
    }

    document.addEventListener('keydown', onKeyPress);

    alreadyRan = true;
}

module.exports = {
    init: init
};
