var applicationCtrl = require('javascript/controllers/application');
var analyticsCtrl = require('javascript/controllers/analytics');
var keyboardController = require('javascript/controllers/keyboard');


//Initialize Controllers
applicationCtrl.init();
analyticsCtrl.init();
keyboardController.init();

//Track Inital View
analyticsCtrl.recordPageOpen();
