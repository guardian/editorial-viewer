/* */ 
(function(process) {
  var debug = process.env.DEBUG_JSBEAUTIFY || process.env.JSBEAUTIFY_DEBUG ? function() {
    console.error.apply(console, arguments);
  } : function() {};
  var fs = require("fs"),
      cc = require("config-chain"),
      beautify = require("../index"),
      mkdirp = require("mkdirp"),
      nopt = require("nopt"),
      path = require("path"),
      knownOpts = {
        "indent_size": Number,
        "indent_char": String,
        "eol": String,
        "indent_level": Number,
        "indent_with_tabs": Boolean,
        "preserve_newlines": Boolean,
        "max_preserve_newlines": Number,
        "space_in_paren": Boolean,
        "space_in_empty_paren": Boolean,
        "jslint_happy": Boolean,
        "space_after_anon_function": Boolean,
        "brace_style": ["collapse", "expand", "end-expand", "expand-strict", "none"],
        "break_chained_methods": Boolean,
        "keep_array_indentation": Boolean,
        "unescape_strings": Boolean,
        "wrap_line_length": Number,
        "wrap_attributes": ["auto", "force"],
        "wrap_attributes_indent_size": Number,
        "e4x": Boolean,
        "end_with_newline": Boolean,
        "comma_first": Boolean,
        "selector_separator_newline": Boolean,
        "newline_between_rules": Boolean,
        "max_char": Number,
        "unformatted": [String, Array],
        "indent_inner_html": [Boolean],
        "indent_scripts": ["keep", "separate", "normal"],
        "extra_liners": [String, Array],
        "version": Boolean,
        "help": Boolean,
        "files": [path, Array],
        "outfile": path,
        "replace": Boolean,
        "quiet": Boolean,
        "type": ["js", "css", "html"],
        "config": path
      },
      shortHands = dasherizeShorthands({
        "s": ["--indent_size"],
        "c": ["--indent_char"],
        "e": ["--eol"],
        "l": ["--indent_level"],
        "t": ["--indent_with_tabs"],
        "p": ["--preserve_newlines"],
        "m": ["--max_preserve_newlines"],
        "P": ["--space_in_paren"],
        "E": ["--space_in_empty_paren"],
        "j": ["--jslint_happy"],
        "a": ["--space_after_anon_function"],
        "b": ["--brace_style"],
        "B": ["--break_chained_methods"],
        "k": ["--keep_array_indentation"],
        "x": ["--unescape_strings"],
        "w": ["--wrap_line_length"],
        "X": ["--e4x"],
        "n": ["--end_with_newline"],
        "C": ["--comma_first"],
        "L": ["--selector_separator_newline"],
        "N": ["--newline_between_rules"],
        "A": ["--wrap_attributes"],
        "i": ["--wrap_attributes_indent_size"],
        "W": ["--max_char"],
        "U": ["--unformatted"],
        "I": ["--indent_inner_html"],
        "S": ["--indent_scripts"],
        "E": ["--extra_liners"],
        "good-stuff": ["--keep_array_indentation", "--keep_function_indentation", "--jslint_happy"],
        "js": ["--type", "js"],
        "css": ["--type", "css"],
        "html": ["--type", "html"],
        "v": ["--version"],
        "h": ["--help"],
        "f": ["--files"],
        "o": ["--outfile"],
        "r": ["--replace"],
        "q": ["--quiet"]
      });
  function verifyExists(fullPath) {
    return fs.existsSync(fullPath) ? fullPath : null;
  }
  function findRecursive(dir, fileName) {
    var fullPath = path.join(dir, fileName);
    var nextDir = path.dirname(dir);
    var result = verifyExists(fullPath);
    if (!result && (nextDir !== dir)) {
      result = findRecursive(nextDir, fileName);
    }
    return result;
  }
  function getUserHome() {
    return process.env.HOME || process.env.USERPROFILE;
  }
  var interpret = exports.interpret = function(argv, slice) {
    var parsed = nopt(knownOpts, shortHands, argv, slice);
    if (parsed.version) {
      console.log(require("../../package.json!systemjs-json").version);
      process.exit(0);
    } else if (parsed.help) {
      usage();
      process.exit(0);
    }
    var cfg = cc(parsed, cleanOptions(cc.env('jsbeautify_'), knownOpts), parsed.config, findRecursive(process.cwd(), '.jsbeautifyrc'), verifyExists(path.join(getUserHome() || "", ".jsbeautifyrc")), __dirname + '/../config/defaults.json').snapshot;
    try {
      checkType(cfg);
      checkFiles(cfg);
      debug(cfg);
      cfg.files.forEach(processInputSync, {cfg: cfg});
    } catch (ex) {
      debug(cfg);
      console.error(ex);
      console.error('Run `' + getScriptName() + ' -h` for help.');
      process.exit(1);
    }
  };
  if (require.main === module) {
    interpret();
  }
  function usage(err) {
    var scriptName = getScriptName();
    var msg = [scriptName + '@' + require("../../package.json!systemjs-json").version, '', 'CLI Options:', '  -f, --file       Input file(s) (Pass \'-\' for stdin)', '  -r, --replace    Write output in-place, replacing input', '  -o, --outfile    Write output to file (default stdout)', '  --config         Path to config file', '  --type           [js|css|html] ["js"]', '  -q, --quiet      Suppress logging to stdout', '  -h, --help       Show this help', '  -v, --version    Show the version', '', 'Beautifier Options:', '  -s, --indent-size             Indentation size [4]', '  -c, --indent-char             Indentation character [" "]', '  -t, --indent-with-tabs        Indent with tabs, overrides -s and -c', '  -e, --eol                     Character(s) to use as line terminators. (default newline - "\\n")', '  -n, --end-with-newline        End output with newline'];
    switch (scriptName.split('-').shift()) {
      case "js":
        msg.push('  -l, --indent-level                Initial indentation level [0]');
        msg.push('  -p, --preserve-newlines           Preserve line-breaks (--no-preserve-newlines disables)');
        msg.push('  -m, --max-preserve-newlines       Number of line-breaks to be preserved in one chunk [10]');
        msg.push('  -P, --space-in-paren              Add padding spaces within paren, ie. f( a, b )');
        msg.push('  -E, --space-in-empty-paren        Add a single space inside empty paren, ie. f( )');
        msg.push('  -j, --jslint-happy                Enable jslint-stricter mode');
        msg.push('  -a, --space-after-anon-function   Add a space before an anonymous function\'s parens, ie. function ()');
        msg.push('  -b, --brace-style                 [collapse|expand|end-expand|none] ["collapse"]');
        msg.push('  -B, --break-chained-methods       Break chained method calls across subsequent lines');
        msg.push('  -k, --keep-array-indentation      Preserve array indentation');
        msg.push('  -x, --unescape-strings            Decode printable characters encoded in xNN notation');
        msg.push('  -w, --wrap-line-length            Wrap lines at next opportunity after N characters [0]');
        msg.push('  -X, --e4x                         Pass E4X xml literals through untouched');
        msg.push('  --good-stuff                      Warm the cockles of Crockford\'s heart');
        msg.push('  -C, --comma-first                 Put commas at the beginning of new line instead of end');
        break;
      case "html":
        msg.push('  -b, --brace-style                 [collapse|expand|end-expand] ["collapse"]');
        msg.push('  -I, --indent-inner-html           Indent body and head sections. Default is false.');
        msg.push('  -S, --indent-scripts              [keep|separate|normal] ["normal"]');
        msg.push('  -w, --wrap-line-length            Wrap lines at next opportunity after N characters [0]');
        msg.push('  -A, --wrap-attributes             Wrap html tag attributes to new lines [auto|force] ["auto"]');
        msg.push('  -i, --wrap-attributes-indent-size Indent wrapped tags to after N characters [indent-level]');
        msg.push('  -p, --preserve-newlines           Preserve line-breaks (--no-preserve-newlines disables)');
        msg.push('  -m, --max-preserve-newlines       Number of line-breaks to be preserved in one chunk [10]');
        msg.push('  -U, --unformatted                 List of tags (defaults to inline) that should not be reformatted');
        msg.push('  -E, --extra_liners                List of tags (defaults to [head,body,/html] that should have an extra newline');
        break;
      case "css":
        msg.push('  -L, --selector-separator-newline        Add a newline between multiple selectors.');
        msg.push('  -N, --newline-between-rules             Add a newline between CSS rules.');
    }
    if (err) {
      msg.push(err);
      msg.push('');
      console.error(msg.join('\n'));
    } else {
      console.log(msg.join('\n'));
    }
  }
  function processInputSync(filepath) {
    var data = '',
        config = this.cfg,
        outfile = config.outfile,
        input;
    if (outfile === true || config.replace) {
      outfile = filepath;
    }
    if (filepath === '-') {
      input = process.stdin;
      input.resume();
      input.setEncoding('utf8');
      input.on('data', function(chunk) {
        data += chunk;
      });
      input.on('end', function() {
        makePretty(data, config, outfile, writePretty);
      });
    } else {
      var dir = path.dirname(outfile);
      mkdirp.sync(dir);
      data = fs.readFileSync(filepath, 'utf8');
      makePretty(data, config, outfile, writePretty);
    }
  }
  function makePretty(code, config, outfile, callback) {
    try {
      var fileType = getOutputType(outfile, config.type);
      var pretty = beautify[fileType](code, config);
      callback(null, pretty, outfile, config);
    } catch (ex) {
      callback(ex);
    }
  }
  function writePretty(err, pretty, outfile, config) {
    if (err) {
      console.error(err);
      process.exit(1);
    }
    if (outfile) {
      if (isFileDifferent(outfile, pretty)) {
        try {
          fs.writeFileSync(outfile, pretty, 'utf8');
          logToStdout('beautified ' + path.relative(process.cwd(), outfile), config);
        } catch (ex) {
          onOutputError(ex);
        }
      } else {
        logToStdout('beautified ' + path.relative(process.cwd(), outfile) + ' - unchanged', config);
      }
    } else {
      process.stdout.write(pretty);
    }
  }
  function isFileDifferent(filePath, expected) {
    try {
      return fs.readFileSync(filePath, 'utf8') !== expected;
    } catch (ex) {
      return true;
    }
  }
  function cleanOptions(data, types) {
    nopt.clean(data, types);
    return data;
  }
  function onOutputError(err) {
    if (err.code === 'EACCES') {
      console.error(err.path + " is not writable. Skipping!");
    } else {
      console.error(err);
      process.exit(0);
    }
  }
  function dasherizeFlag(str) {
    return str.replace(/^\-+/, '').replace(/_/g, '-');
  }
  function dasherizeShorthands(hash) {
    Object.keys(hash).forEach(function(key) {
      var val = hash[key][0];
      if (key.length === 1 && val.indexOf('_') > -1) {
        hash[dasherizeFlag(val)] = val;
      }
    });
    return hash;
  }
  function getOutputType(outfile, configType) {
    if (outfile && /\.(js|css|html)$/.test(outfile)) {
      return outfile.split('.').pop();
    }
    return configType;
  }
  function getScriptName() {
    return path.basename(process.argv[1]);
  }
  function checkType(parsed) {
    var scriptType = getScriptName().split('-').shift();
    debug("executable type:", scriptType);
    var parsedType = parsed.type;
    debug("parsed type:", parsedType);
    if (!parsedType) {
      debug("type defaulted:", scriptType);
      parsed.type = scriptType;
    }
  }
  function checkFiles(parsed) {
    var argv = parsed.argv;
    if (!parsed.files) {
      parsed.files = [];
    } else {
      if (argv.cooked.indexOf('-') > -1) {
        parsed.files.some(removeDashedPath);
      }
    }
    if (argv.remain.length) {
      argv.remain.forEach(function(f) {
        parsed.files.push(path.resolve(f));
      });
    }
    if ('string' === typeof parsed.outfile && !parsed.files.length) {
      parsed.files.push(parsed.outfile);
      parsed.replace = true;
    }
    if (argv.original.indexOf('-') > -1) {
      parsed.files.push('-');
    }
    if (!parsed.files.length) {
      throw 'Must define at least one file.';
    }
    debug('files.length ' + parsed.files.length);
    parsed.files.forEach(testFilePath);
    return parsed;
  }
  function removeDashedPath(filepath, i, arr) {
    var found = filepath.lastIndexOf('-') === (filepath.length - 1);
    if (found) {
      arr.splice(i, 1);
    }
    return found;
  }
  function testFilePath(filepath) {
    try {
      if (filepath !== "-") {
        fs.statSync(filepath);
      }
    } catch (err) {
      throw 'Unable to open path "' + filepath + '"';
    }
  }
  function logToStdout(str, config) {
    if (typeof config.quiet === "undefined" || !config.quiet) {
      console.log(str);
    }
  }
})(require("process"));
