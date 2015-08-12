
var viewers = require('javascript/components/viewer');
var orientationButtons = require('javascript/components/orientationButtons');
var toolbar = require('javascript/components/toolbar');

//Apply initial config
orientationButtons.init();
toolbar.init(document.getElementById('toolbar'));
viewers.updateViewer('primary', 'mobile-portrait');
