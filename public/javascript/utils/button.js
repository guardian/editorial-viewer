
function markSelected(attributeName, activeValue) {

    var buttons = document.querySelectorAll('[data-' + attributeName + ']');

    for (var i = 0; i < buttons.length; ++i) {
        buttons[i].classList.remove('is-selected');
    }

    var activeEls = document.querySelectorAll('[data-' + attributeName + '="' + activeValue + '"]');
    for (var ii = 0; ii < activeEls.length; ++ii) {
        activeEls[ii].classList.add('is-selected');
    }
}

function bindClickToAttributeName(attributeName, fn) {
    var els = document.querySelectorAll('[data-' + attributeName + ']');

    for (var i = 0; i < els.length; ++i) {
        els[i].addEventListener('click', fn);
    }
}

function bindClickToModeUpdate(attributeName, fn) {
    var els = document.querySelectorAll('[data-' + attributeName + ']');
    var bindClick = function(el) {
       var mode = el.dataset.switchmode;
       el.addEventListener('click', function() {
           fn(mode);
       });
   };

    for (var i = 0; i < els.length; ++i) {
        bindClick(els[i]);
    }
}

function styleWithAttributeNameAndValue(attributeName, attributeValue, styleAttribute, styleValue) {
    var els = document.querySelectorAll('[data-' + attributeName + '="' + attributeValue + '"]');

    for (var i = 0; i < els.length; ++i) {
        els[i].style[styleAttribute] = styleValue;
    }
}

module.exports = {
    markSelected:                     markSelected,
    bindClickToAttributeName:         bindClickToAttributeName,
    bindClickToModeUpdate:            bindClickToModeUpdate,
    styleWithAttributeNameAndValue:   styleWithAttributeNameAndValue,
};
