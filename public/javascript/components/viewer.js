
var viewerEl = document.getElementById('viewer');

var currentViewPortConfig;
var currentViewPortName;
var currentViewerUrl;

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

    viewerEl.addEventListener('load', function(e){
        var iframeLocation = e.target.contentWindow.location;
        if (iframeLocation.origin !== "null" || iframeLocation.protocol.indexOf('http') !== -1) {
            currentViewerUrl = iframeLocation.href;
        }
    });
}

function reloadiFrame() {
    if (!currentViewerUrl) {
        viewerEl.src = viewerEl.src;
    } else {
        viewerEl.src = currentViewerUrl;
    }
}

function updateUrl(url) {
    viewerEl.src = url;
    currentViewerUrl = url;
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

module.exports = {
    updateViewer: updateViewer,
    updateUrl: updateUrl
};
