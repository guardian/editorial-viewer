var applicationCtrl = require('javascript/controllers/application');
var analyticsCtrl = require('javascript/controllers/analytics');

//Initialize Controllers
applicationCtrl.init();
analyticsCtrl.init();

//Track Inital View
analyticsCtrl.recordPageOpen();
