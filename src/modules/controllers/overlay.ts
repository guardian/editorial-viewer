const selectors = {
    overlay: '[data-role=overlay]',
    okButton: '.overlay__ok',
    closeButton: '.overlay__close',
}
const getEl = (name: keyof typeof selectors) => document.querySelector(selectors[name]) as HTMLElement;

const overlayEl = () => getEl('overlay');
const visibleClass = 'overlay--visible'
const hiddenClass = 'overlay--hidden'
const okButton = () => getEl('okButton');
const closeButton = () => getEl('closeButton');
const buttons = function(){return [okButton(), closeButton()]}

export function showOverlay() {
    overlayEl().className = visibleClass

    buttons().forEach(function(e) {
        e?.addEventListener('click', hideOverlay)
    });
};

export function hideOverlay() {
    overlayEl().className = hiddenClass;

    buttons().forEach(function(e) {
        e?.removeEventListener('click', hideOverlay);
    });
};

export default {
    showOverlay,
    hideOverlay,
}
