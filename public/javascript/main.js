var modeCtrl = require('javascript/controllers/viewerMode');
var analyticsCtrl = require('javascript/controllers/analytics');


var viewers = require('javascript/components/viewers/viewers');
var toolbar = require('javascript/components/toolbar');
var enableDesktop = require('javascript/components/enableDesktop');


//Initialize Controllers

modeCtrl.init({
    onUpdate: function() {
        viewers.render();
    }
});

//Initialize Components
toolbar.init();
viewers.init();
analyticsCtrl.init();
enableDesktop.init();

//Track Inital View
analyticsCtrl.recordPageOpen();
