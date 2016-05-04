var Reqwest = require('reqwest');

function appPreviewRequest() {
    return Reqwest({
        url: 'https://' + window.location.hostname + '/send-email?path=' + window._originalPath,
        crossOrigin: true,
        withCredentials: true,
        method: 'post'
    });
}

module.exports = {
    appPreviewRequest: appPreviewRequest
}
