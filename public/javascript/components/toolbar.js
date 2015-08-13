
var modeCtrl = require('javascript/controllers/viewerMode');
var viewers = require('javascript/components/viewers/viewers');
var analyticsCtrl = require('javascript/controllers/analytics');

var toolbarEl;

function renderMenuItem(itemName) {
    var el = document.createElement('li');

    el.setAttribute('data-switchmode', itemName);
    el.innerHTML = itemName;
    el.addEventListener('click', handleClick);
    el.classList.add('tool-bar__button');

    if (itemName === modeCtrl.getMode()) {
        el.classList.add('is-selected');
    }

    return el;
}

function renderMenu() {
    if (!toolbarEl) {
        console.log("Can't render without el");
        return;
    }

    toolbarEl.innerHTML = '';
    toolbarEl.appendChild(renderMenuItem('mobile'));
    if (modeCtrl.isDesktopActive()) {
        toolbarEl.appendChild(renderMenuItem('desktop'));
    }

}

function handleClick(e) {
    var mode = e.target.dataset.switchmode;

    if (mode === "desktop") {
        analyticsCtrl.recordDesktopViewed();
    }
    modeCtrl.updateMode(mode);
    renderMenu();
}

function init(el) {
    toolbarEl = el;
    renderMenu();
}

function enableDesktop() {
    menuitems.desktop = true;

    renderMenu();
}

module.exports = {
    init: init,
    enableDesktop: enableDesktop,
    render: renderMenu
};
