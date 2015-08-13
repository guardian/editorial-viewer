var switchEls = document.querySelectorAll('[data-switchPrimaryViewer]');
var modeCtrl = require('javascript/controllers/viewerMode');
var analyticsCtrl = require('javascript/controllers/analytics');

function resetButtonStyles() {
    var buttonEls = document.querySelectorAll('[data-switchPrimaryViewer]');

    for (i = 0; i < buttonEls.length; ++i) {
        buttonEls[i].classList.remove('is-selected');
    }
}

function init() {
    //Bind To orientation switch buttons

    for (i = 0; i < switchEls.length; ++i) {
        switchEls[i].addEventListener('click' , function(e){
            analyticsCtrl.recordOrientationChange();
            resetButtonStyles();
            modeCtrl.setSingleViewerOrientation(e.target.dataset.switchprimaryviewer);
            e.target.classList.add('is-selected');
        })
    }
}

function hide() {
    for (i = 0; i < switchEls.length; ++i) {
        switchEls[i].style.display = 'none';
    }
}

function show() {
    for (i = 0; i < switchEls.length; ++i) {
        switchEls[i].style.display = 'inline-block';
    }
}

module.exports =  {
    init : init,
    hide: hide,
    show: show
}
