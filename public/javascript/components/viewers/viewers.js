var modeCtrl = require('javascript/controllers/viewerMode');

var viewer = require('javascript/components/viewers/viewer');
var orientationButtons = require('javascript/components/viewers/orientationButtons');

function render() {

    if (modeCtrl.getMode() === 'desktop') {
        viewer.updateViewer('primary', 'desktop');
        viewer.updateViewer('secondary', 'hidden');
        orientationButtons.hide();
    } else {

        if (modeCtrl.getSingleViewerOrientation() === 'landscape') {
            viewer.updateViewer('primary', 'mobile-landscape');
        } else {
            viewer.updateViewer('primary', 'mobile-portrait');
        }

        viewer.updateViewer('secondary', 'hidden');
        orientationButtons.show();
    }
}

function init() {
    orientationButtons.init();
    render();
}

module.exports = {
    render: render,
    init: init
};
