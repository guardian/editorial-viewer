
var modeCtrl = require('javascript/controllers/viewerMode');
var viewers = require('javascript/components/viewers/viewers');

var menuitems = {
    mobile: true,
    desktop: true
};

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

    Object.keys(menuitems).forEach(function(menuItem) {
        if (!menuitems[menuItem]) {
            return;
        }
        toolbarEl.appendChild(renderMenuItem(menuItem));
    })
}

function handleClick(e) {
    var mode = e.target.dataset.switchmode;
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