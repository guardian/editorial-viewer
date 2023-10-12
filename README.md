# Editorial Viewer service

Mobile first preview of Guardian content. [https://viewer.gutools.co.uk](https://viewer.gutools.co.uk)

## Setup
Run `./script/setup` from the project root.

You will need `composer` credentials from Janus to ensure `viewer.private.conf` downloads correctly.

You may need to run `sudo chown -R $(whoami):admin /etc/gu` so that you have access to `/etc/gu`.

The javascript bundle (public/build.js - file is gitignored) need to be built for the frontend to work properly when running locally:
 - install the version of npm specified in [./nvmrc](./nvmrc) - this is best done using nvm : `nvm use`
 - install the dependencies : `npm i`
 - run the bundle script: `npm run bundlejs`

## Running
Run `./script/start` from the project root.

The app will then be accessible locally at: https://viewer.local.dev-gutools.co.uk/ and articles previews are accessible at the following urls:

Preview: ```https://viewer.local.dev-gutools.co.uk/preview/{articlepath}```

Live: ```https://viewer.local.dev-gutools.co.uk/live/{articlepath}```

For example:
- ```https://viewer.local.dev-gutools.co.uk/preview/uk-news/2015/aug/11/london-underground-strike-august-night-tube-dispute```
- ```https://viewer.local.dev-gutools.co.uk/live/uk-news/2015/aug/11/london-underground-strike-august-night-tube-dispute```

### Debugging
Run `./script/start --debug` from the project root to expose port 5005 for debugging.

## Developing

- [`npm`](http://npmjs.com) is used for dependency management for client-side tooling
- [`jspm`](http://jspm.io) is used for client-side dependency management, transpiling, and configuring the SystemJS loader
- [`sass`](http://sass-lang.com) is used for compiling SCSS sources to CSS
- [`sbt`](http://www.scala-sbt.org) is used to compile and run the application in development mode. Note: JDK 8 is required
- [Play framework](https://playframework.com) is used with scala to run and serve the application server side

### Project structure

    app                   - Scala sources
    ├── com
    │   ├── gu
    │   │   ├── viewer
    │   │   │   ├── views - HTML view templates (twirl)
    │
    public
    ├── javascript        - Client-side Javascript sources
    ├── styles            - SCSS sources

Running the application in development mode will automatically watch for Scala source changes, and recompile:
```
$ sbt run
```

To automatically watch for changes and recompile SASS when SCSS sources change, use:
```
$ npm run watch
```