module.exports = {
    set: function set(name, value, options) {
        options = options || {};
        var def = [encodeURIComponent(name) + '=' + encodeURIComponent(value)];
        if (options.path) def.push('path=' + options.path);
        if (options.domain) def.push('domain=' + options.domain);
        def.push('max-age=' + 60*60*24*365);
        def.push('expires=' + Date.now() + (60*60*24*365));
        def = def.join(';');
        document.cookie = def;
        return def;
    },
    get: function (n) {
        var match = n + "=", c = '', ca = document.cookie.split(';'), i;
        for (var i = 0; i < ca.length, c=ca[i]; i++) {
            if (c.indexOf(match) === 0) {
                return c.substring(match.length, c.length);
            }
        }

        return null;
    },
    delete: function (n) {
        this.set(n, "", -1);
    }
}
