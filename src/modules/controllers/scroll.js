

async function init() {
    await new Promise(resolve => setTimeout(() => resolve(), 100));

    const viewers = Array.from(document.getElementsByClassName('viewer'));
    viewers.forEach((viewer, viewerIndex) => {
        console.log('Adding event listener', viewer, viewer.src, viewer.contentWindow, viewerIndex);

        viewer.contentWindow.addEventListener('scroll', () => {
            console.log('scrollin')
            requestAnimationFrame(() => {
                const { documentElement } = viewer.contentDocument;
                const scrollTopMax = documentElement.scrollHeight - viewer.contentWindow.innerHeight;
                const scrollPosition = documentElement.scrollTop / scrollTopMax;
                const otherViewers = viewers.filter(v => v !== viewer);
                otherViewers.forEach((otherViewer) => {
                    otherViewerDocumentElement = otherViewer.contentDocument.documentElement;
                    otherViewerScrollTopMax = otherViewerDocumentElement.scrollHeight - otherViewer.contentWindow.innerHeight;
                    otherViewerDocumentElement.scrollTo(0, otherViewerScrollTopMax * scrollPosition);
                })
            })
        });
    })

    // TODO: something something mutation observer
}


module.exports = {
    init
};
