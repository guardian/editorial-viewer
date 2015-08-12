var twoColumnBreak = 1160;
var twoColumn = false;

var onUpdateFn;

function init(options) {

    onUpdateFn = options.onUpdate;

    window.addEventListener('resize', function(){
        if (window.innerWidth >= 1160 && !twoColumn) {
            updateColumnsEnabled(true);
        } else if (window.innerWidth < 1160 && twoColumn) {
            updateColumnsEnabled(false);
        }
    });
}

function updateColumnsEnabled(value) {
    twoColumn = value;

    if (onUpdateFn) {
        onUpdateFn();
    }
}

function isTwoColumn() {
    return twoColumn;
}

module.exports = {
    init: init,
    isTwoColumn: isTwoColumn
}