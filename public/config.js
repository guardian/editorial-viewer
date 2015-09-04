System.config({
  baseURL: "/assets",
  defaultJSExtensions: true,
  transpiler: "babel",
  babelOptions: {
    "optional": [
      "runtime"
    ]
  },
  paths: {
    "github:*": "jspm_packages/github/*",
    "npm:*": "jspm_packages/npm/*"
  },
  bundles: {
    "build.js": [
      "javascript/main.js",
      "javascript/controllers/analytics.js",
      "javascript/controllers/application.js",
      "javascript/controllers/keyboard.js",
      "javascript/components/viewer.js",
      "javascript/utils/localStorage.js",
      "javascript/utils/button.js",
      "npm:localforage@1.2.10",
      "npm:localforage@1.2.10/src/localforage",
      "npm:localforage@1.2.10/src/drivers/indexeddb",
      "npm:promise@5.0.0",
      "npm:localforage@1.2.10/src/drivers/websql",
      "npm:localforage@1.2.10/src/drivers/localstorage",
      "npm:promise@5.0.0/index",
      "npm:localforage@1.2.10/src/utils/serializer",
      "npm:asap@1.0.0",
      "npm:promise@5.0.0/core",
      "npm:asap@1.0.0/asap",
      "github:jspm/nodelibs-process@0.1.1",
      "github:jspm/nodelibs-process@0.1.1/index",
      "npm:process@0.10.1",
      "npm:process@0.10.1/browser"
    ]
  },

  map: {
    "babel": "npm:babel-core@5.8.23",
    "babel-runtime": "npm:babel-runtime@5.8.20",
    "core-js": "npm:core-js@1.1.3",
    "localforage": "npm:localforage@1.2.10",
    "github:jspm/nodelibs-path@0.1.0": {
      "path-browserify": "npm:path-browserify@0.0.0"
    },
    "github:jspm/nodelibs-process@0.1.1": {
      "process": "npm:process@0.10.1"
    },
    "npm:asap@1.0.0": {
      "process": "github:jspm/nodelibs-process@0.1.1"
    },
    "npm:babel-runtime@5.8.20": {
      "process": "github:jspm/nodelibs-process@0.1.1"
    },
    "npm:core-js@1.1.3": {
      "fs": "github:jspm/nodelibs-fs@0.1.2",
      "process": "github:jspm/nodelibs-process@0.1.1",
      "systemjs-json": "github:systemjs/plugin-json@0.1.0"
    },
    "npm:localforage@1.2.10": {
      "path": "github:jspm/nodelibs-path@0.1.0",
      "process": "github:jspm/nodelibs-process@0.1.1",
      "promise": "npm:promise@5.0.0"
    },
    "npm:path-browserify@0.0.0": {
      "process": "github:jspm/nodelibs-process@0.1.1"
    },
    "npm:promise@5.0.0": {
      "asap": "npm:asap@1.0.0"
    }
  }
});
