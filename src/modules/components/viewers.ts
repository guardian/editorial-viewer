import errorController from '../controllers/error';
import * as scrollController from '../controllers/scroll';
import type { Mode } from '../modes';
import buttonUtil from '../utils/button';

const viewersContainer = document.getElementsByClassName('viewers')[0];
const viewerEls = [...document.querySelectorAll('iframe.viewer, .viewer > iframe')] as HTMLIFrameElement[];
let currentViewerUrl = viewerEls[0].src;
let currentMode: Mode = 'mobile-portrait';
let adBlockDisabled = false;
let desktopEnabled = false;
const mobileModeRegex = /mobile-/;

function updateViewers(mode: Mode) {
    var isAnimated = false;
    var preventRefresh = false;

    if (currentMode !== mode) {
        // We have a change of mode, test for special cases where we can animate
        if (mobileModeRegex.test(mode) && mobileModeRegex.test(currentMode)) {
            isAnimated = true;
        }
    }

    if (mobileModeRegex.test(mode) && viewerEls.length === 1) {
        const newViewer = document.createElement('iframe');
        newViewer.src = generateUrl(currentViewerUrl);
        newViewer.addEventListener('load', onViewerLoad);
        viewersContainer.insertBefore(newViewer, viewersContainer.firstChild!);
        viewerEls.unshift(newViewer);
        updateVisibleViewers();
        scrollController.updateViewers(viewerEls);

        viewerEls[0].title = "Mobile viewer";
        viewerEls[1].title = "Desktop viewer";
    }

    if (['reader', 'social-share'].includes(mode)) {
        preventRefresh = true;
    }

    if (['desktop', 'reader', 'social-share'].includes(mode)) {
        if (viewerEls.length === 2) {
            if (mode === 'desktop') {
                viewersContainer.removeChild(viewerEls[0]);
                viewerEls.shift();
                viewerEls[0].parentElement!.style.display = '';
            } else {
                // Move overlay to hidden part of DOM so we don't have to recreate it
                const overlay = document.getElementsByClassName('is-desktop__overlay')[0]!;
                const overlayStorage = document.getElementById('desktop-overlay-storage-area')!;
                overlayStorage.appendChild(overlay);

                viewersContainer.removeChild(viewerEls[1].parentElement!);
                viewerEls.pop();
            }

            scrollController.updateViewers(viewerEls);
            viewerEls[0].title = "Viewer";
        }
    } else {
        if (viewerEls.length === 1) {
            const newViewerWrapper = document.createElement('div');
            viewersContainer.appendChild(newViewerWrapper);
            newViewerWrapper.className = 'viewer is-desktop';
            const newViewer = document.createElement('iframe');
            newViewerWrapper.appendChild(newViewer);
            newViewer.src = generateUrl(currentViewerUrl);
            newViewer.addEventListener('load', onViewerLoad);
            viewerEls.push(newViewer);

            if(!desktopEnabled) {
                const overlay = document.getElementsByClassName('is-desktop__overlay')[0]!;
                newViewerWrapper.append(overlay);
            }

            updateVisibleViewers();
            scrollController.updateViewers(viewerEls);
        }

        viewerEls[0].title = "Mobile viewer";
        viewerEls[1].title = "Desktop viewer";
    }

    if (mode === 'reader') {
        if (currentMode === 'social-share') {
            updateUrl(currentViewerUrl);

            // Hack to allow time for page to load before applying reader mode styling
            setTimeout(() => {
                enableReader();
            }, 200);
        } else {
            enableReader();
        }
    }

    if (mode === 'social-share') {
        enableSocialShare();
        preventRefresh = true;
    }

    currentMode = mode;

    restyleViewer(isAnimated, preventRefresh);
};

function reloadiFrame() {
    errorController.hideError();
    updateUrl(currentViewerUrl);
};

function generateUrl(baseUrl: string) {
    return `${baseUrl}${adBlockDisabled ? '' : '#noads'}`;
};

function updateUrl(url: string) {
    currentViewerUrl = url;

    var newiFrameUrl = generateUrl(url);

    viewerEls.forEach(viewerEl => {
        viewerEl.src = 'about:blank';

        setTimeout(function() {
            viewerEl.src = newiFrameUrl;
        }, 100);
    });
};

function printViewer() {
    try {
        viewerEls[0].contentWindow?.print();
    } catch (e) {
        console.log("Can't communicate with iframe ", e);
    }
};

function enableReader() {
    const viewerEl = viewerEls[0];

    if (!viewerEl.contentDocument) {
        console.log("Can't enable Reader mode, viewer content document is inaccessible");
        return;
    }

    try {
        var printStyleSheets = viewerEl.contentDocument.querySelectorAll('link[media=\'print\']');

        for (var i = 0; i < printStyleSheets.length; i++) {
            printStyleSheets[i].setAttribute('media', 'all');
        }

        var styleLink = document.createElement('link');
        styleLink.href = '/assets/styles/readerMode.css';
        styleLink.rel = 'stylesheet';
        styleLink.setAttribute('media', 'screen');
        styleLink.type = 'text/css';

        viewerEl.contentDocument?.body.appendChild(styleLink);
    } catch (e) {
        console.log("Can't enable Reader mode: ", e);
    }
};

function enableSocialShare() {
    const viewerEl = viewerEls[0];

    if (!viewerEl.contentDocument) {
        console.log("Can't enable Social share mode, viewer content document is inaccessible");
        return;
    }

    try {
        var printStyleSheets = viewerEl.contentDocument.querySelectorAll('link[media=\'print\']');

        for (var i = 0; i < printStyleSheets.length; i++) {
            printStyleSheets[i].setAttribute('media', 'all');
        }

        var styleLink = document.createElement('link');
        styleLink.href = '/assets/styles/socialShareMode.css';
        styleLink.rel = 'stylesheet';
        styleLink.setAttribute('media', 'screen');
        styleLink.type = 'text/css';

        viewerEl.contentDocument.body.appendChild(styleLink);

        /* Facebook header */
        remove(viewerEl, 'fbHeader');
        var fbHeader = document.createElement('h1');
        fbHeader.id = 'fbHeader';
        fbHeader.innerHTML = 'Facebook';
        viewerEl.contentDocument.body.appendChild(fbHeader);

        /* Facebook card */
        var ogImage = viewerEl.contentDocument.querySelector('meta[property=\'og:image\']') as HTMLMetaElement;
        var ogTitle = viewerEl.contentDocument.querySelector('meta[property=\'og:title\']') as HTMLMetaElement;
        var ogDesc = viewerEl.contentDocument.querySelector('meta[property=\'og:description\']') as HTMLMetaElement;
        var author = viewerEl.contentDocument.querySelectorAll('meta[name=\'author\']')[0] as HTMLMetaElement;

        remove(viewerEl, 'fbCard');

        var fbCard = document.createElement('div');
        fbCard.id = 'fbCard';
        fbCard.innerHTML = '' +
            '<div class=\'image\'><img src=\'' + ogImage.content + '\'></img></div>' +
            '<div class=\'header\'>' +
            '  <div class=\'title\'><span>' + ogTitle.content + '</span></div>' +
            '  <div class=\'desc\'>' + ogDesc.content + '</div>' +
            '  <div class=\'author\'> theguardian.com | By ' + (author ? author.content : 'unknown')  + '</div>' +
            ' </div>';


        viewerEl.contentDocument.body.appendChild(fbCard);

         /* Twitter header */
        remove(viewerEl, 'twHeader');
        var twHeader = document.createElement('h1');
        twHeader.id = 'twHeader';
        twHeader.innerHTML = 'Twitter';
        viewerEl.contentDocument.body.appendChild(twHeader);

        /* Twitter card */
        const twCardType = viewerEl.contentDocument.querySelector('meta[name=\'twitter:card\']') as HTMLMetaElement;
        const twImageEl = viewerEl.contentDocument.querySelector('meta[name=\'twitter:image\']') as HTMLMetaElement || ogImage;
        const twTitleEl = viewerEl.contentDocument.querySelector('meta[name=\'twitter:title\']') as HTMLMetaElement || ogTitle;
        const twDescEl = viewerEl.contentDocument.querySelector('meta[name=\'twitter:description\']') as HTMLMetaElement || ogDesc;

        // Twitter tries to ensure that any trails it displays fit across two lines.
        // In practice, this leads to a trail between roughly 114 - 140 characters in
        // length. We truncate the description here to provide a conservative
        // approximation.
        const twitterTrailLimit = 114
        const twDesc = twDescEl.content.length > twitterTrailLimit
          ? (twDescEl.content.slice(0, twitterTrailLimit) + "…")
          : twDescEl.content

        remove(viewerEl, 'twCard');
        var twCard = document.createElement('div');
        twCard.id = 'twCard';
        twCard.className = twCardType.content;
        twCard.innerHTML = '' +
            '<div class=\'image\'><img src=\'' + twImageEl.content + '\'></img></div>' +
            '<div class=\'header\'>' +
            '  <div class=\'title\'>' + twTitleEl.content + '</div>' +
            '  <div class=\'desc\'>' + twDesc + '</div>' +
            '  <div class=\'author\'> theguardian.com </div>' +
            ' </div>';

        viewerEl.contentDocument.body.appendChild(twCard);
        viewerEl.contentDocument.body.style.margin = '20px';
    } catch (e) {
        console.log("Can't enable Social share mode: ", e);
    }
};

function remove(frame: HTMLIFrameElement, id: string) {
    if (!frame.contentDocument) {
        console.log(`Could not remove "${id}", iframe content document is inaccessible`);
    }

    var previous = frame.contentDocument?.getElementById(id);
    if (previous) {
        previous.parentNode?.removeChild(previous);
    }
};

function restyleViewer(isAnimated: boolean, preventRefresh: boolean) {
    const viewerEl = viewerEls[0];

    var transitionEndHandler = function() {
        viewerEl.removeEventListener('transitionend', transitionEndHandler);
        viewerEl.classList.remove('is-animated');
        updateVisibleViewers();

        if (!preventRefresh) {
            reloadiFrame();
        }
    };

    viewerEl.className = 'viewer is-' + currentMode;

    if (isAnimated) {
        viewerEl.classList.add('is-animated');
        viewerEl.addEventListener('transitionend', transitionEndHandler);
    }

    if (!isAnimated && !preventRefresh) {
        reloadiFrame();
    }
};

function scrollViewer(scrollByAmount: number) {
    viewerEls.forEach(viewerEl => {
        viewerEl.contentWindow?.scrollBy(0, scrollByAmount * viewerEl.clientHeight / 1.5);
    });
};

function scrollViewerDown() {
    scrollViewer(1);
};

function scrollViewerUp() {
    scrollViewer(-1);
};

function onViewerLoad(e: Event) {
    var iframe = (e.target as HTMLIFrameElement);
    var iframeLocation = iframe.contentWindow?.location;
    if (iframeLocation && (iframeLocation.origin !== 'null' || iframeLocation.protocol.indexOf('http') !== -1)) {
        currentViewerUrl = iframeLocation.origin + iframeLocation.pathname;
        addBlankToLinks(iframe);
    }

    // If we have mode than one viewer and this is not the first, skip the test
    if (iframe !== viewerEls[0]) {
        return;
    }

    if (currentMode === 'reader') {
        enableReader();
    }
    if (currentMode === 'social-share') {
        enableSocialShare();
    }
};

function detectMobileAndRedirect() {
    if (window.screen && window.screen.width <= 768) {
        if (window._actualUrl) {
            window.location.href = window._actualUrl;
        } else {
            window.location.href = viewerEls[0].src;
        }
    }
};

function enableAdBlock() {
    adBlockDisabled = false;

    reloadiFrame();
};

function enableDesktop() {
    desktopEnabled = true;

    document.querySelector('.is-desktop__overlay')?.remove();
};

function disableAdBlock() {
    adBlockDisabled = true;

    reloadiFrame();
};

function addBlankToLinks(iframe: HTMLIFrameElement) {
    var iframeDoc = iframe.contentDocument;
    var anchors = iframeDoc?.querySelectorAll('.js-article__body a, .article-body-viewer-selector a') as NodeListOf<HTMLAnchorElement>;
    anchors.forEach(anchor => {
        // If href doesn't contain gutools or theguardian (i.e: links to guardian pages) add blank
        if (!/gutools|theguardian/.test(anchor.origin)) {
            anchor.setAttribute('target', '_blank');
            anchor.setAttribute('rel', 'noopener noreferrer');
        }
    });
};

function updateVisibleViewers() {
    requestAnimationFrame(() => {
        if (viewerEls.length === 1 && currentMode !== 'desktop') {
            // No further action required
            return;
        }

        const threshold = currentMode === 'mobile-landscape' ? 1360 : 1120;
        const desktopButton = document.querySelector('[data-switch-mode="desktop"]') as HTMLElement;

        if (window.innerWidth < threshold) {
            viewerEls[1].parentElement!.style.display = 'none';
            desktopButton.style.display = '';
        } else {
            if (currentMode === 'desktop') {
                updateViewers('mobile-portrait');
                buttonUtil.markSelected('switch-mode', 'mobile-portrait');
            }

            viewerEls[1].parentElement!.style.display = '';
            desktopButton.style.display = 'none';
        }
    });
};

function init() {
    detectMobileAndRedirect();
    viewerEls.forEach(viewerEl => viewerEl.addEventListener('load', onViewerLoad));
    updateVisibleViewers();
    window.addEventListener('resize', () => {
        updateVisibleViewers();
    });
    scrollController.updateViewers(viewerEls);
};

export default {
    updateViewers,
    updateUrl,
    disableAdBlock,
    enableAdBlock,
    enableDesktop,
    printViewer,
    scrollViewerUp,
    scrollViewerDown,
    init,
};
