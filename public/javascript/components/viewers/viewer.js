var viewports = {
    "mobile-portrait": {
        width: "330px",
        height: "568px"
    },
    "mobile-landscape": {
        width: "568px",
        height: "320px"
    },
    "desktop": {
        width: "",
        height: ""
    }
};

var viewers = {
    "primary": "hidden",
    "secondary" : "hidden"
};

function applyViewerStyle(viewerEl, viewportName) {

    if (viewportName === "hidden") {
        viewerEl.style.display = "none";
        return;
    }

    viewerEl.style.display= "inline-block";

    viewerEl.className = 'viewer is-' + viewportName;

    viewerEl.style.width = viewports[viewportName].width;
    viewerEl.style.height = viewports[viewportName].height;
}


function applyConfig() {

    var viewerEls = document.querySelectorAll("[data-viewerName]");

    for (var i = 0; i < viewerEls.length; ++i) {
        var viewerEl = viewerEls[i];
        var viewportName = viewers[viewerEl.dataset.viewername];
        applyViewerStyle(viewerEl, viewportName)
    }

}

function updateViewer(viewerName, viewport) {
    viewers[viewerName] = viewport;
    applyConfig();
}

module.exports = {
    updateViewer: updateViewer
}
