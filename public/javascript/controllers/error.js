var errorEl = document.getElementById('errorbar');

var visibleClass = "error-bar--active";
var inVisibleClass = "error-bar";

function showError(text) {
    errorEl.innerText = text;
    errorEl.className = visibleClass;
}

function hideError() {
    errorEl.innerText = '';
    errorEl.className = inVisibleClass;
}

module.exports = {
    showError: showError,
    hideError: hideError
};
