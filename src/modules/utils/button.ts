import type { Mode } from '../modes'

function markSelected(attributeName: string, activeValue: string) {

    var buttons = document.querySelectorAll('[data-' + attributeName + ']');

    for (var i = 0; i < buttons.length; ++i) {
        buttons[i].classList.remove('is-selected');
    }

    var activeEls = document.querySelectorAll('[data-' + attributeName + '="' + activeValue + '"]');
    for (var ii = 0; ii < activeEls.length; ++ii) {
        activeEls[ii].classList.add('is-selected');
    }
}

function bindClickToAttributeName(attributeName: string, fn: () => void) {
    var els = document.querySelectorAll('[data-' + attributeName + ']');

    for (var i = 0; i < els.length; ++i) {
        els[i].addEventListener('click', fn);
    }
}

function bindClickToModeUpdate(attributeName: string, fn: (mode: Mode) => void) {
    var els = document.querySelectorAll('[data-' + attributeName + ']') as NodeListOf<HTMLElement>;
    var bindClick = function(el: HTMLElement) {
       var mode = el.dataset.switchMode as Mode;
       el.addEventListener('click', function() {
           fn(mode);
       });
    };

    for (var i = 0; i < els.length; ++i) {
        bindClick(els[i]);
    }
}

export default {
    markSelected,
    bindClickToAttributeName,
    bindClickToModeUpdate,
};
