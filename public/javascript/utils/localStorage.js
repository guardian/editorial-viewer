var localforage = require('localforage');

var ENABLED_PAGES_MAX = 100;
var ENABLED_PAGES_KEY = 'desktopEnabled';

var ADBLOCK_STATUS_KEY = 'adblockEnabled';

function getEnabledHrefs() {
    return localforage.getItem(ENABLED_PAGES_KEY);
}

function saveEnabledHrefs(hrefs) {
    return localforage.setItem(ENABLED_PAGES_KEY, hrefs);
}

function saveAdBlockStatus(status) {
    return localforage.setItem(ADBLOCK_STATUS_KEY, status);
}

function getAdBlockStatus() {
    return localforage.getItem(ADBLOCK_STATUS_KEY);
}

function addEnabledHref(href) {

    getEnabledHrefs().then(function(hrefs) {
        //Needs to be an array
        if (!Array.isArray(hrefs)) {
            hrefs = [];
        }

        //Already in there, just return
        if (hrefs.indexOf(href) !== -1) {
            return;
        }

        //Add item
        hrefs.push(href);

        //Is it too long?
        if (hrefs.length > ENABLED_PAGES_MAX) {
            hrefs = hrefs.slice(hrefs.length - ENABLED_PAGES_MAX, hrefs.length);
        }

        //Save it
        saveEnabledHrefs(hrefs);
    });
}

function removeEnabledHref(href) {

    getEnabledHrefs().then(function(hrefs){

        if (!Array.isArray(hrefs) || hrefs.indexOf(href) === -1) {
            //Didn't find the href... just return;
            return;
        }

        hrefs.splice(hrefs.indexOf(href), 1);
        saveEnabledHrefs(hrefs);
    });
}

module.exports = {
    addEnabledHref:    addEnabledHref,
    removeEnabledHref: removeEnabledHref,
    getEnabledHrefs:   getEnabledHrefs,
    saveAdBlockStatus: saveAdBlockStatus,
    getAdBlockStatus:  getAdBlockStatus
};
