var messageEl = document.getElementById('messageBar');

var visibleClass = "message-bar--active";
var inVisibleClass = "message-bar";

function showmessage(text) {
    messageEl.innerText = text;
    messageEl.className = visibleClass;
    messageEl.addEventListener('click', hidemessage)
}

function hidemessage() {
    messageEl.innerText = '';
    messageEl.className = inVisibleClass;
    messageEl.removeEventListener('click', hidemessage)
}

module.exports = {
    showmessage: showmessage,
    hidemessage: hidemessage
};
