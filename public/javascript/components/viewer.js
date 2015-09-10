var tween = require('tween');
var viewerEl = document.getElementById('viewer');

var currentViewPortConfig;
var currentViewPortName;

function updateViewer(viewportName, viewportConfig) {

    var isAnimated = false;

    if (currentViewPortConfig && currentViewPortConfig !== viewportConfig) {
        //We have a change of viewport, test for special cases where we can animate
        if (viewportConfig.isMobile && currentViewPortConfig.isMobile) {
            isAnimated = true;
        }
    }

    currentViewPortConfig = viewportConfig;
    currentViewPortName = viewportName;

    restyleViewer(isAnimated);
}

function reloadiFrame() {
    viewerEl.src = viewerEl.src;
}

function printViewer() {
    try {
      viewerEl.contentWindow.print();
    } catch (e) {
      console.log("Can't communicate with iframe")
    }
}

function restyleViewer(isAnimated) {

    var transitionEndHandler = function() {
        viewerEl.removeEventListener('transitionend', transitionEndHandler);
        viewerEl.classList.remove('is-animated');
        reloadiFrame();
    };

    viewerEl.className = 'viewer is-' + currentViewPortName;

    if (isAnimated) {
        viewerEl.classList.add('is-animated');
        viewerEl.addEventListener('transitionend', transitionEndHandler);
    }

    viewerEl.style.width = currentViewPortConfig.width;
    viewerEl.style.height = currentViewPortConfig.height;

    if (!isAnimated) {
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
    scrollViewerDown: scrollViewerDown
};
