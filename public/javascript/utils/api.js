function appPreviewRequest() {
    return new Promise(function(y, n) {
        // make actual API call
        return y()
    })
}

module.exports = {
    appPreviewRequest: appPreviewRequest
}
