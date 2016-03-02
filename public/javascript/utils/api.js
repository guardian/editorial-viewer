var Reqwest = require('reqwest');

function appPreviewRequest() {
    return Reqwest({
        url: '/send-email?path=' + window._originalPath,
        method: 'post'
    });
}

module.exports = {
    appPreviewRequest: appPreviewRequest
}
