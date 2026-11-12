var Reqwest = require('reqwest');

function appPreviewRequest() {
    const token = window._csrfToken;
    const csrfParam = token.name + '=' + token.value;
    var url = 'https://' + window.location.hostname + '/send-email?path=' + encodeURIComponent(window._originalPath);

    if (window._shortPath) {
        url += '&shortPath=' + encodeURIComponent(window._shortPath);
    }

    url += '&' + csrfParam;

    return Reqwest({
        url: url,
        crossOrigin: true,
        withCredentials: true,
        method: 'post',

    });
}

module.exports = {
    appPreviewRequest: appPreviewRequest
}
