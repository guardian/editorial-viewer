var analyticsCtrl = require('../controllers/analytics.js');
var viewerEl = document.getElementById('viewer');
var currentViewerUrl = viewerEl.src;

var errorController = require('../controllers/error.js');

var currentViewPortConfig;
var currentViewPortName = 'mobile-portrait';

var adsBlocked;

function updateViewer(viewportName, viewportConfig) {

    if (viewportName === currentViewPortName) {
        return;
    }

    var isAnimated = false;
    var preventRefresh = false;

    if (currentViewPortConfig && currentViewPortConfig !== viewportConfig) {
        //We have a change of viewport, test for special cases where we can animate
        if (viewportConfig.isMobile && currentViewPortConfig.isMobile) {
            isAnimated = true;
        }
    }

    if (viewportConfig.isReader) {
        enableReader();
        preventRefresh = true;
    }

    currentViewPortConfig = viewportConfig;
    currentViewPortName = viewportName;

    restyleViewer(isAnimated, preventRefresh);
}

function reloadiFrame() {
    errorController.hideError();
    updateUrl(currentViewerUrl);
}

function updateUrl(url) {
    currentViewerUrl = url;

    var newiFrameUrl = url;

    if (adsBlocked) {
        newiFrameUrl += '#noads';
    } else {
        newiFrameUrl += '#';
    }

    viewerEl.src = newiFrameUrl;
    viewerEl.contentWindow.location.reload();
}

function printViewer() {
    try {
        viewerEl.contentWindow.print();
        analyticsCtrl.recordPrint();
    } catch (e) {
        console.log('Can\'t communicate with iframe ', e);
    }
}

function enableReader() {
    try {
        var printStyleSheets = viewerEl.contentDocument.querySelectorAll('link[media=\'print\']');

        for (var i = 0; i < printStyleSheets.length; i++) {
            printStyleSheets[i].setAttribute('media', 'all');
        }

        var styleLink = document.createElement('link');
        styleLink.href = '/assets/styles/readerMode.css';
        styleLink.rel = 'stylesheet';
        styleLink.setAttribute('media', 'screen');
        styleLink.type = 'text/css';

        viewerEl.contentDocument.body.appendChild(styleLink);

    } catch (e) {
        console.log('Can\'t enable Reader mode: ', e);
    }
}

function restyleViewer(isAnimated, preventRefresh) {

    var transitionEndHandler = function() {
        viewerEl.removeEventListener('transitionend', transitionEndHandler);
        viewerEl.classList.remove('is-animated');

        if (!preventRefresh) {
            reloadiFrame();
        }
    };

    viewerEl.className = 'viewer is-' + currentViewPortName;

    if (isAnimated) {
        viewerEl.classList.add('is-animated');
        viewerEl.addEventListener('transitionend', transitionEndHandler);
    }

    if (!isAnimated && !preventRefresh) {
        reloadiFrame();
    }
}

function scrollViewer(scrollByAmount) {
    viewerEl.contentWindow.scrollBy(0, scrollByAmount);
}

function scrollViewerDown() {
    scrollViewer(viewerEl.clientHeight / 1.5);
}

function scrollViewerUp() {
    scrollViewer(-1 * viewerEl.clientHeight / 1.5);
}

function onViewerLoad(e) {
    var iframeLocation = e.target.contentWindow.location;
    if (iframeLocation.origin !== 'null' || iframeLocation.protocol.indexOf('http') !== -1) {
        currentViewerUrl = iframeLocation.origin + iframeLocation.pathname;
    }

    if (currentViewPortName === 'reader') {
        enableReader();
    }
}

function detectMobileAndRedirect() {
    if (window.screen && window.screen.width <= 768) {
        if (window._actualUrl) {
            window.location.href = window._actualUrl;
        } else {
            window.location.href = viewerEl.src;
        }
    }
}

function enableAdBlock() {
    adsBlocked = true;

    reloadiFrame();
}

function disableAdBlock() {
    adsBlocked = false;

    reloadiFrame();
}

function init() {
    detectMobileAndRedirect();
    viewerEl.addEventListener('load', onViewerLoad);
}

module.exports = {
    updateViewer:     updateViewer,
    updateUrl:        updateUrl,
    disableAdBlock:   disableAdBlock,
    enableAdBlock:    enableAdBlock,
    printViewer:      printViewer,
    scrollViewerUp:   scrollViewerUp,
    scrollViewerDown: scrollViewerDown,
    enableReader:     enableReader,
    init:             init
};
