import viewers from '../components/viewers';
import applicationController from './application'
import * as scrollController from './scroll';
let alreadyRan = false;

function onKeyPress(e: KeyboardEvent) {
    if ((e.ctrlKey || e.metaKey) && (e.key === 'p' || e.key === 'U+0050')) {
        viewers.printViewer();
        e.preventDefault();
    }

    if (e.key === 'ArrowUp' || e.key === "Up" || (e.key === ' ' && e.shiftKey) ) {
        scrollController.scrollViewerUp();
        e.preventDefault();
    }

    if (e.key === 'ArrowDown' || e.key === "Down" || (e.key === ' ' && !e.shiftKey)) {
        scrollController.scrollViewerDown();
        e.preventDefault();
    }

    if (e.key === '1' || e.key === 'U+0031') {
        applicationController.setMode('mobile-portrait');
        e.preventDefault();
    }

    if (e.key === '2' || e.key === 'U+0032') {
        applicationController.setMode('mobile-landscape');
        e.preventDefault();
    }

    if (e.key === '3' || e.key === 'U+0033') {
        applicationController.setMode('reader');
        e.preventDefault();
    }

    if (e.key === '4' || e.key === 'U+0035') {
        applicationController.setMode('social-share');
        e.preventDefault();
    }

    if (e.key === '5' || e.key === 'U+0034') {
        applicationController.setMode('desktop');
        e.preventDefault();
    }
}

function onWheel (e: WheelEvent) {
    scrollController.scrollPixels(e.deltaY)
}

export function init() {
    if (alreadyRan) {
        console.log("Already init'd keyboardController");
        return;
    }

    document.addEventListener('keydown', onKeyPress);
    const viewers = document.getElementsByClassName('viewers')[0] as HTMLElement;
    viewers.addEventListener('wheel', onWheel);

    alreadyRan = true;
}
