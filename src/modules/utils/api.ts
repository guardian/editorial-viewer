async function appPreviewRequest() {
    const token = window._csrfToken;
    const csrfParam = `${token.name}=${token.value}`;

    const url = `https://${window.location.hostname}/send-email?path=${window._originalPath}&${csrfParam}`;
    return fetch(url, { method: 'post', credentials: 'include', mode: 'cors'})
}

export default {
    appPreviewRequest,
}
