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
        height: "auto"
    }
};



function applyViewerStyle(viewer, viewportName) {

    if (viewportName === "hidden") {
        viewer.style.display = "none";
    } else {
        viewer.style.display= "block";
    }

    viewer.className = 'viewer is-' + viewportName;

    viewer.style.width = viewports[viewportName].width;
    viewer.style.height = viewports[viewportName].height;
}


function applyConfig(viewerConfig) {

    var viewers = document.querySelectorAll("[data-viewerName]");

    for (i = 0; i < viewers.length; ++i) {
        var viewer = viewers[i];
        var viewportName = viewerConfig[viewer.dataset.viewername];

        applyViewerStyle(viewer, viewportName)
    }

}

module.exports = {
    applyConfig: applyConfig
}

