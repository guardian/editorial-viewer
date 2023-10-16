const path = require('path');

module.exports = {
  entry: './src/index.js',
  mode: 'development',
  watch:true,
  watchOptions: {
    ignored: /node_modules/,
  },
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'public'),
  },
};