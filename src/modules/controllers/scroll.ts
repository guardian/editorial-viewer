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

export function init() {
    viewers = Array.from(document.getElementsByClassName('viewer')) as HTMLIFrameElement[];
    viewers.forEach((viewer) => {
        addScrollListener(viewer);

        viewer.addEventListener('load', () => {
            addScrollListener(viewer);
        });

        viewer.addEventListener('mouseenter', () => {
            activeViewer = viewer;
        });

        viewer.addEventListener('mouseleave', () => {
            activeViewer = null;
        });
    });
};
