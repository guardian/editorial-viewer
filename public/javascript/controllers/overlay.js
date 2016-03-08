var overlayEl = function() {return document.querySelector('[data-role=overlay]')}
var visibleClass = 'overlay--visible'
var hiddenClass = 'overlay--hidden'
var okButton = function() {return document.querySelector('.overlay__ok')}
var closeButton = function() {return document.querySelector('.overlay__close')}
var buttons = function(){return [okButton(), closeButton()]}

function showOverlay() {
    overlayEl().className = visibleClass

    buttons().forEach(function(e) {
        e.addEventListener('click', hideOverlay)
    })
}

function hideOverlay() {
    overlayEl().className = hiddenClass

    buttons().forEach(function(e) {
        e.removeEventListener('click', hideOverlay)
    })
}

module.exports = {
    showOverlay: showOverlay,
    hideOverlay: hideOverlay
}
