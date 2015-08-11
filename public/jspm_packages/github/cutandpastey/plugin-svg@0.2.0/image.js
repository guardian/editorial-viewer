/*
  Image plugin
  Adapted from Miller Medeiros requirejs-plugins
  https://github.com/millermedeiros/requirejs-plugins/blob/master/src/image.js
*/
exports.build = false;
exports.build = false;
exports.fetch = function(load) {
  return new Promise(function(resolve, reject) {
    var object = document.createElement('object');
    object.type = 'image/svg+xml';
    object.data = load.address;
    load.metadata.img = object;
    resolve('');
  });
};

exports.instantiate = function(load) {
  return load.metadata.img;
};
