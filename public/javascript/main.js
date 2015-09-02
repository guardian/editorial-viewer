var applicationCtrl = require('javascript/controllers/application');
var analyticsCtrl = require('javascript/controllers/analytics');
var historyCtrl = require('javascript/controllers/history');

//Initialize Controllers
applicationCtrl.init();
analyticsCtrl.init();
historyCtrl.init();

//Track Inital View
analyticsCtrl.recordPageOpen();
