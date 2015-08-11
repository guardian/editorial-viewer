/* */ 
"format cjs";
function get_beautify(js_beautify, css_beautify, html_beautify) {
  var beautify = function(src, config) {
    return js_beautify.js_beautify(src, config);
  };
  beautify.js = js_beautify.js_beautify;
  beautify.css = css_beautify.css_beautify;
  beautify.html = html_beautify.html_beautify;
  beautify.js_beautify = js_beautify.js_beautify;
  beautify.css_beautify = css_beautify.css_beautify;
  beautify.html_beautify = html_beautify.html_beautify;
  return beautify;
}
if (typeof define === "function" && define.amd) {
  define(["./lib/beautify","./lib/beautify-css","./lib/beautify-html"], function(js_beautify, css_beautify, html_beautify) {
    return get_beautify(js_beautify, css_beautify, html_beautify);
  });
} else {
  (function(mod) {
    var js_beautify = require("./lib/beautify");
    var css_beautify = require("./lib/beautify-css");
    var html_beautify = require("./lib/beautify-html");
    mod.exports = get_beautify(js_beautify, css_beautify, html_beautify);
  })(module);
}
