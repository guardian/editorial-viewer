var errorEl = document.getElementById('errorbar') as HTMLElement;

var visibleClass = "error-bar--active";
var inVisibleClass = "error-bar";

function showError(text: string) {
    errorEl.innerText = text;
    errorEl.className = visibleClass;
}

function hideError() {
    errorEl.innerText = '';
    errorEl.className = inVisibleClass;
}

export default {
    showError,
    hideError,
};
