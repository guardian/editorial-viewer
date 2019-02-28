System.config({
  baseURL: "/assets",
  defaultJSExtensions: true,
  transpiler: "babel",
  babelOptions: {
    "optional": [
      "runtime",
      "optimisation.modules.system"
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
      "javascript/controllers/history.js",
      "javascript/controllers/keyboard.js",
      "javascript/utils/button.js",
      "javascript/modes.js",
      "javascript/controllers/error.js",
      "javascript/utils/localStorage.js",
      "javascript/controllers/overlay.js",
      "javascript/components/viewer.js",
      "javascript/utils/api.js",
      "npm:localforage@1.2.10.js",
      "npm:reqwest@2.0.5.js",
      "npm:localforage@1.2.10/src/localforage.js",
      "npm:reqwest@2.0.5/reqwest.js",
      "npm:promise@5.0.0.js",
      "npm:localforage@1.2.10/src/drivers/indexeddb.js",
      "npm:localforage@1.2.10/src/drivers/localstorage.js",
      "npm:localforage@1.2.10/src/drivers/websql.js",
      "github:jspm/nodelibs-process@0.1.2.js",
      "npm:promise@5.0.0/index.js",
      "npm:localforage@1.2.10/src/utils/serializer.js",
      "github:jspm/nodelibs-process@0.1.2/index.js",
      "npm:promise@5.0.0/core.js",
      "npm:asap@1.0.0.js",
      "npm:process@0.11.2.js",
      "npm:asap@1.0.0/asap.js",
      "npm:process@0.11.2/browser.js"
    ]
  },

  map: {
    "babel": "npm:babel-core@5.8.25",
    "babel-runtime": "npm:babel-runtime@5.8.25",
    "core-js": "npm:core-js@1.2.1",
    "localforage": "npm:localforage@1.2.10",
    "reqwest": "npm:reqwest@2.0.5",
    "github:jspm/nodelibs-assert@0.1.0": {
      "assert": "npm:assert@1.3.0"
    },
    "github:jspm/nodelibs-path@0.1.0": {
      "path-browserify": "npm:path-browserify@0.0.0"
    },
    "github:jspm/nodelibs-process@0.1.2": {
      "process": "npm:process@0.11.2"
    },
    "github:jspm/nodelibs-util@0.1.0": {
      "util": "npm:util@0.10.3"
    },
    "npm:asap@1.0.0": {
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:assert@1.3.0": {
      "util": "npm:util@0.10.3"
    },
    "npm:babel-runtime@5.8.25": {
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:core-js@1.2.1": {
      "fs": "github:jspm/nodelibs-fs@0.1.2",
      "process": "github:jspm/nodelibs-process@0.1.2",
      "systemjs-json": "github:systemjs/plugin-json@0.1.0"
    },
    "npm:inherits@2.0.1": {
      "util": "github:jspm/nodelibs-util@0.1.0"
    },
    "npm:localforage@1.2.10": {
      "path": "github:jspm/nodelibs-path@0.1.0",
      "process": "github:jspm/nodelibs-process@0.1.2",
      "promise": "npm:promise@5.0.0"
    },
    "npm:path-browserify@0.0.0": {
      "process": "github:jspm/nodelibs-process@0.1.2"
    },
    "npm:process@0.11.2": {
      "assert": "github:jspm/nodelibs-assert@0.1.0"
    },
    "npm:promise@5.0.0": {
      "asap": "npm:asap@1.0.0"
    },
    "npm:reqwest@2.0.5": {
      "child_process": "github:jspm/nodelibs-child_process@0.1.0",
      "fs": "github:jspm/nodelibs-fs@0.1.2",
      "process": "github:jspm/nodelibs-process@0.1.2",
      "systemjs-json": "github:systemjs/plugin-json@0.1.0"
    },
    "npm:util@0.10.3": {
      "inherits": "npm:inherits@2.0.1",
      "process": "github:jspm/nodelibs-process@0.1.2"
    }
  }
});
