var analyticsCtrl = require('../controllers/analytics.js');
var viewerEl = document.getElementById('viewer');

var currentViewPortConfig;
var currentViewPortName;

function updateViewer(viewportName, viewportConfig) {

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
    viewerEl.src = viewerEl.src;

    if (currentViewPortName === 'reader') {
        enableReader();
    }
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

    viewerEl.style.width = currentViewPortConfig.width;
    viewerEl.style.height = currentViewPortConfig.height;

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

module.exports = {
    updateViewer: updateViewer,
    printViewer: printViewer,
    scrollViewerUp: scrollViewerUp,
    scrollViewerDown: scrollViewerDown,
    enableReader: enableReader
};
