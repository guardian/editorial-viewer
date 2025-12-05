let viewers: HTMLIFrameElement[] = [];
let activeViewer: HTMLIFrameElement | null = null;

function addScrollListener(viewer: HTMLIFrameElement) {
    viewer.contentWindow?.addEventListener('scroll', () => {
        if (viewer !== activeViewer) {
            return;
        }

        requestAnimationFrame(() => {
            const viewerWindow = viewer.contentWindow;
            const viewerDocument = viewer.contentDocument?.documentElement;

            if (!viewerDocument || !viewerWindow) {
                console.error('Source iframe could not be accessed');
                return;
            }

            const scrollTopMax = viewerDocument.scrollHeight - viewerWindow.innerHeight;
            const scrollPosition = viewerDocument.scrollTop / scrollTopMax;
            const otherViewers = viewers.filter(v => v !== viewer);
            otherViewers.forEach((otherViewer) => {
                const otherViewerDocument = otherViewer.contentDocument?.documentElement;
                const otherViewerWindow = otherViewer.contentWindow;

                if (!otherViewerDocument || !otherViewerWindow) {
                    console.error('Target iframe could not be accessed');
                    return;
                }

                const otherViewerScrollTopMax = otherViewerDocument.scrollHeight - otherViewerWindow.innerHeight;
                otherViewerDocument.scrollTo(0, otherViewerScrollTopMax * scrollPosition);
            });
        });
    });
};

function addEventListenersToViewer(viewer: HTMLIFrameElement) {
    viewer.addEventListener('load', () => {
        addScrollListener(viewer);
    });

    const onEnter = () => { activeViewer = viewer };

    viewer.addEventListener('mouseenter', onEnter);
    viewer.addEventListener('touchstart', onEnter);
    viewer.addEventListener('mouseleave', () => {
        activeViewer = null;
    });
};

export function updateViewers(updatedViewers: HTMLIFrameElement[]) {
    const newViewers = updatedViewers.filter(uv => !viewers.includes(uv));
    newViewers.forEach(newViewer => addEventListenersToViewer(newViewer))
    viewers = [...updatedViewers];
};
