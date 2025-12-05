import localforage from 'localforage';

const ENABLED_PAGES_MAX = 100;
const ENABLED_PAGES_KEY = 'desktopEnabled';
const ADBLOCK_DISABLED_UNTILL_KEY = 'adblockDisabled';

function getEnabledHrefs() {
    return localforage.getItem(ENABLED_PAGES_KEY).then(hrefs => {
        return Array.isArray(hrefs) ? hrefs : [];
    });
}

function saveEnabledHrefs(hrefs: string[]) {
    return localforage.setItem(ENABLED_PAGES_KEY, hrefs);
}

function saveAdBlockDisabledUntil(status: number | false) {
    return localforage.setItem(ADBLOCK_DISABLED_UNTILL_KEY, status);
}

function getAdBlockStatus() {
    return localforage.getItem(ADBLOCK_DISABLED_UNTILL_KEY);
}

function addEnabledHref(href: string) {

    getEnabledHrefs().then(function(hrefs) {
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

function removeEnabledHref(href: string) {

    getEnabledHrefs().then(function(hrefs) {

        if (!Array.isArray(hrefs) || hrefs.indexOf(href) === -1) {
            //Didn't find the href... just return;
            return;
        }

        hrefs.splice(hrefs.indexOf(href), 1);
        saveEnabledHrefs(hrefs);
    });
}

export default {
    addEnabledHref,
    removeEnabledHref,
    getEnabledHrefs,
    saveAdBlockDisabledUntil,
    getAdBlockStatus,
};
