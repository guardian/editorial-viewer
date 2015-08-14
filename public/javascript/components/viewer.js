
var viewerEl = document.getElementById('viewer');

var currentViewPortName;

function applyViewerStyle(viewportName, viewportConfig) {

    viewerEl.className = 'viewer is-' + viewportName;

    viewerEl.style.width = viewportConfig.width;
    viewerEl.style.height = viewportConfig.height;

    currentViewPortName = viewportName;
}

module.exports = {
    updateViewer: applyViewerStyle
};
