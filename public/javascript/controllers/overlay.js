var overlayEl = document.querySelector('.overlay')
var visibleClass = 'overlay--visible'
var hiddenClass = 'overlay--hidden'

function showOverlay() {
    overlayEl.className = visibleClass
}

function hideOverlay() {
    overlayEl.className = inVisibleClass
}

module.exports = {
    showOverlay: showOverlay,
    hideOverlay: hideOverlay
}
