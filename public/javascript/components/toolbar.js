
var modeCtrl = require('javascript/controllers/viewerMode');
var viewers = require('javascript/components/viewers/viewers');
var analyticsCtrl = require('javascript/controllers/analytics');

var toolbarBtns = document.querySelectorAll('[data-switchmode]');

function init() {
    for (var i = 0; i < toolbarBtns.length; ++i) {
        toolbarBtns[i].addEventListener('click', handleClick);
    }
}

function handleClick(e) {
    var mode = e.target.dataset.switchmode;

    if (mode === 'desktop') {
        analyticsCtrl.recordDesktopViewed();
    }
    modeCtrl.updateMode(mode);
    updateActiveStates(mode);
}

function updateActiveStates(mode) {
    for (var i = 0; i < toolbarBtns.length; ++i) {
        toolbarBtns[i].classList.remove('is-selected');
    }

    var activeEl = document.querySelector('[data-switchmode="' + mode + '"]');
    activeEl && activeEl.classList.add('is-selected');
}

function enableDesktop() {
    menuitems.desktop = true;
}

module.exports = {
    init:          init,
    enableDesktop: enableDesktop,
};
