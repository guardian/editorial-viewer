var enableBtns = document.querySelectorAll('[data-enabledesktop]');
var modeCtrl = require('javascript/controllers/viewerMode');


function init() {
    for (i = 0; i < enableBtns.length; ++i) {
        enableBtns[i].addEventListener('click' , function(e){
            modeCtrl.enableDesktop();
        })
    }
}

module.exports = {
    init: init
}
