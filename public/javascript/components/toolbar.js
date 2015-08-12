
var viewer = require('./viewer');
var orientationButtons = require('javascript/components/orientationButtons');

var menuitems = {
    mobile: true,
    desktop: true
};

var activeitem = 'mobile';

var toolbarEl;

function renderMenuItem(itemName) {
    var el = document.createElement('li');

    el.setAttribute('data-switchmode', itemName);
    el.innerHTML = itemName;
    el.addEventListener('click', handleClick);
    el.classList.add('tool-bar__button');

    if (activeitem === itemName) {
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
    activeitem = mode;

    if (mode === 'desktop') {
        viewer.updateViewer('primary', 'desktop');
        orientationButtons.hide();
    } else {
        viewer.updateViewer('primary', 'mobile-portrait');
        orientationButtons.show();
    }

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
    enableDesktop: enableDesktop
};