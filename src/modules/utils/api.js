var Reqwest = require('reqwest');

function appPreviewRequest() {
    const token = window._csrfToken;
    const csrfParam = token.name + '=' + token.value;

    return Reqwest({
        url: 'https://' + window.location.hostname + '/send-email?path=' + window._originalPath + '&' + csrfParam,
        crossOrigin: true,
        withCredentials: true,
        method: 'post',

    });
}

module.exports = {
    appPreviewRequest: appPreviewRequest
}
