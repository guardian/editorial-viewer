# Editorial Viewer service

Mobile first preview of Guardian content.

# Installation

## Set up Nginx
Follow the [dev-nginx README](https://github.com/guardian/dev-nginx). There is an nginx mapping file in `nginx/`.

## Install client-side dependencies and build JS/CSS
```
$ npm install
```

## Starting the app
```
$ sbt run
```

The app will then be accessible locally at: https://viewer.local.dev-gutools.co.uk/ and articles previews are accessible at the following urls:

Preview: https://viewer.local.dev-gutools.co.uk/preview/{articlepath}
Live: https://viewer.local.dev-gutools.co.uk/live/{articlepath}

e.g. https://viewer.local.dev-gutools.co.uk/preview/uk-news/2015/aug/11/london-underground-strike-august-night-tube-dispute
or https://viewer.local.dev-gutools.co.uk/live/uk-news/2015/aug/11/london-underground-strike-august-night-tube-dispute


## Developing

Instead of the asset pipeline in Play we're using  more standard frontend technologies:
`npm` for dependency management, CommonJS modules and `webpack` to bundle our JS up into
a file that is then imported by the browser. You'll find the source files in `assets/javascripts`
and the build files in `public/javascript`. Styles are pre-processed with SASS.

You'll want to have webpack running in the background to trigger the build task when you modify
the SASS.

```
npm run watch
```
