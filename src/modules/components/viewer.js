var viewerEl = document.getElementById('viewer');
var currentViewerUrl = viewerEl.src;

var errorController = require('../controllers/error.js');

var currentViewPortConfig;
var currentViewPortName = 'mobile-portrait';

var adBlockDisabled;

function updateViewer(viewportName, viewportConfig) {

    var isAnimated = false;
    var preventRefresh = false;

    if (currentViewPortConfig && currentViewPortConfig !== viewportConfig) {
        //We have a change of viewport, test for special cases where we can animate
        if (viewportConfig.isMobile && currentViewPortConfig.isMobile) {
            isAnimated = true;
        }
    }

    if (viewportConfig.isReader) {
        enableReader();
        preventRefresh = true;
    }

    if (viewportConfig.isSocial) {
        enableSocialShare();
        preventRefresh = true;
    }

    currentViewPortConfig = viewportConfig;
    currentViewPortName = viewportName;

    restyleViewer(isAnimated, preventRefresh);
}

function reloadiFrame() {
    errorController.hideError();
    updateUrl(currentViewerUrl);
}

function updateUrl(url) {
    currentViewerUrl = url;

    var newiFrameUrl = url;

    if (adBlockDisabled) {
        newiFrameUrl += '#';
    } else {
        newiFrameUrl += '#noads';
    }

    viewerEl.src = 'about:blank';

    setTimeout(function() {
        viewerEl.src = newiFrameUrl;
    }, 100);

}

function printViewer() {
    try {
        viewerEl.contentWindow.print();
    } catch (e) {
        console.log('Can\'t communicate with iframe ', e);
    }
}

function enableReader() {
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

        viewerEl.contentDocument.body.appendChild(styleLink);

    } catch (e) {
        console.log('Can\'t enable Reader mode: ', e);
    }
}

function enableSocialShare() {
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
        var ogImage = viewerEl.contentDocument.querySelector('meta[property=\'og:image\']');
        var ogTitle = viewerEl.contentDocument.querySelector('meta[property=\'og:title\']');
        var ogDesc = viewerEl.contentDocument.querySelector('meta[property=\'og:description\']');
        var author = viewerEl.contentDocument.querySelectorAll('meta[name=\'author\']')[0];

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
        const twCardType = viewerEl.contentDocument.querySelector('meta[name=\'twitter:card\']');
        const twImageEl = viewerEl.contentDocument.querySelector('meta[name=\'twitter:image\']') || ogImage
        const twTitleEl = viewerEl.contentDocument.querySelector('meta[name=\'twitter:title\']') || ogTitle
        const twDescEl = viewerEl.contentDocument.querySelector('meta[name=\'twitter:description\']') || ogDesc

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

    } catch (e) {
        console.log('Can\'t enable Social share mode: ', e);
    }
}

function remove(frame, id) {
    var previous = frame.contentDocument.getElementById(id);
    if (previous) {
        previous.parentNode.removeChild(previous);
    }
}

function restyleViewer(isAnimated, preventRefresh) {

    var transitionEndHandler = function() {
        viewerEl.removeEventListener('transitionend', transitionEndHandler);
        viewerEl.classList.remove('is-animated');

        if (!preventRefresh) {
            reloadiFrame();
        }
    };

    viewerEl.className = 'viewer is-' + currentViewPortName;

    if (isAnimated) {
        viewerEl.classList.add('is-animated');
        viewerEl.addEventListener('transitionend', transitionEndHandler);
    }

    if (!isAnimated && !preventRefresh) {
        reloadiFrame();
    }
}

function scrollViewer(scrollByAmount) {
    viewerEl.contentWindow.scrollBy(0, scrollByAmount);
}

function scrollViewerDown() {
    scrollViewer(viewerEl.clientHeight / 1.5);
}

function scrollViewerUp() {
    scrollViewer(-1 * viewerEl.clientHeight / 1.5);
}

function onViewerLoad(e) {
    var iframeLocation = e.target.contentWindow.location;
    if (iframeLocation.origin !== 'null' || iframeLocation.protocol.indexOf('http') !== -1) {
        currentViewerUrl = iframeLocation.origin + iframeLocation.pathname;
        addBlankToLinks();
    }

    if (currentViewPortName === 'reader') {
        enableReader();
    }
    if (currentViewPortName === 'social-share') {
        enableSocialShare();
    }
}

function detectMobileAndRedirect() {
    if (window.screen && window.screen.width <= 768) {
        if (window._actualUrl) {
            window.location.href = window._actualUrl;
        } else {
            window.location.href = viewerEl.src;
        }
    }
}

function enableAdBlock() {
    adBlockDisabled = false;

    reloadiFrame();
}

function disableAdBlock() {
    adBlockDisabled = true;

    reloadiFrame();
}

function addBlankToLinks() {
    var iframe = document.getElementById('viewer');
    var iframeDoc = iframe.contentDocument;
    var ancs = iframeDoc.querySelectorAll('.js-article__body a, .article-body-viewer-selector a');
    for (var i = 0; i < ancs.length; i++) {
        // If href doesn't contain gutools or theguardian (i.e: links to guardian pages) add blank
        if (!/gutools|theguardian/.test(ancs[i].origin)) {
            ancs[i].setAttribute('target', '_blank');
            ancs[i].setAttribute('rel', 'noopener noreferrer');
        }
    }
}

function init() {
    detectMobileAndRedirect();
    viewerEl.addEventListener('load', onViewerLoad);
}

module.exports = {
    updateViewer:     updateViewer,
    updateUrl:        updateUrl,
    disableAdBlock:   disableAdBlock,
    enableAdBlock:    enableAdBlock,
    printViewer:      printViewer,
    scrollViewerUp:   scrollViewerUp,
    scrollViewerDown: scrollViewerDown,
    enableReader:     enableReader,
    enableSocialShare: enableSocialShare,
    init:             init
};
