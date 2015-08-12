
var resizeCtrl = require('javascript/controllers/resize');
var modeCtrl = require('javascript/controllers/viewerMode');

var viewers = require('javascript/components/viewers/viewers');
var toolbar = require('javascript/components/toolbar');

//Initialize Controllers
resizeCtrl.init({
    onUpdate: function() {
        viewers.render();
    }
});
modeCtrl.init({
    onUpdate: function() {
        viewers.render();
    }
});

//Initialize Components
toolbar.init(document.getElementById('toolbar'));
viewers.init();


