let viewers: HTMLIFrameElement[] = [];
let recentViewer: HTMLIFrameElement | null = null;
let activeViewer: HTMLIFrameElement | null = null;

function scrollOtherViewers(viewer: HTMLIFrameElement) {
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
}

function addScrollListener(viewer: HTMLIFrameElement) {
    viewer.contentWindow?.addEventListener('scroll', () => {
        if (viewer !== activeViewer) {
            return;
        }

        requestAnimationFrame(() => {
            scrollOtherViewers(viewer);
        });
    });
};

function addEventListenersToViewer(viewer: HTMLIFrameElement) {
    viewer.addEventListener('load', () => {
        addScrollListener(viewer);
    });

    const onEnter = () => {
        activeViewer = viewer
        recentViewer = viewer;
    };

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

    if (!activeViewer) {
        activeViewer = viewers[0];
    }
};

export function scrollPixels(scrollByAmount: number) {
    const viewer = recentViewer ?? viewers[0];
    viewer.contentWindow?.scrollBy(0, scrollByAmount);
    scrollOtherViewers(viewer);
};

function scrollPage(scrollByAmount: number) {
    const viewer = recentViewer ?? viewers[0];
    const pixelAmount = scrollByAmount * viewer.clientHeight / 1.5;
    scrollPixels(pixelAmount);
};

export function scrollViewerDown() {
    scrollPage(1);
};

export function scrollViewerUp() {
    scrollPage(-1);
};
