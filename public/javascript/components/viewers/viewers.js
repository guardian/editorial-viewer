var modeCtrl = require('javascript/controllers/viewerMode');

var viewer = require('javascript/components/viewers/viewer');

function render() {
    viewer.updateViewer('primary', modeCtrl.getMode());
    viewer.updateViewer('secondary', 'hidden');
}

function init() {
    render();
}

module.exports = {
    render: render,
    init:   init
};
