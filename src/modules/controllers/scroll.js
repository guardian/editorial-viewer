let viewers = [];
let activeViewer = null;

function addScrollListener(viewer) {
    viewer.contentWindow.addEventListener('scroll', () => {
        if (viewer !== activeViewer) {
            return;
        }

        requestAnimationFrame(() => {
            const { documentElement } = viewer.contentDocument;
            const scrollTopMax = documentElement.scrollHeight - viewer.contentWindow.innerHeight;
            const scrollPosition = documentElement.scrollTop / scrollTopMax;
            const otherViewers = viewers.filter(v => v !== viewer);
            otherViewers.forEach((otherViewer) => {
                otherViewerDocumentElement = otherViewer.contentDocument.documentElement;
                otherViewerScrollTopMax = otherViewerDocumentElement.scrollHeight - otherViewer.contentWindow.innerHeight;
                otherViewerDocumentElement.scrollTo(0, otherViewerScrollTopMax * scrollPosition);
            });
        });
    });
};

function init() {
    viewers = Array.from(document.getElementsByClassName('viewer'));
    viewers.forEach((viewer) => {
        addScrollListener(viewer);

        viewer.addEventListener('load', () => {
            addScrollListener(viewer);
        });

        viewer.addEventListener('mouseenter', (event) => {
            activeViewer = viewer;
        });

        viewer.addEventListener('mouseleave', (event) => {
            activeViewer = null;
        });
    });
}

module.exports = {
    init,
};
