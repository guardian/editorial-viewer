//good to have the ability to switch for debugging
const STABLE_BRANCH       = 'master';
const DIST_BRANCH         = 'dist';
const OUTPUT_CSS_FILENAME = 'all.css';

import Q        from  'q';
import fs       from  'fs';
import path     from 'path';
import {spawn}  from 'child-process-promise';
import colors   from 'colors';
import glob     from 'glob';
import mversion from 'mversion';

let pReadFile   = Q.denodeify(fs.readFile);
let pWriteFile  = Q.denodeify(fs.writeFile);
let pUnlink     = Q.denodeify(fs.unlink);
let pGlob       = Q.denodeify(glob);
let pUpdate     = Q.denodeify(mversion.update);

let printProgress = (childProcess)=> {
  childProcess.stdout.pipe(process.stdout);
  childProcess.stderr.pipe(process.stderr);
};

let handleError = (err) => {
  console.log('-----------------------'.red);
  console.log(err);
  console.log('-----------------------'.red);
  process.exit(1);
};


export default (releaseType) => {
  return Q.async(function* (){

    try {

      if (!releaseType) {
        throw new Error('Usage ./bin/release <version-number | major | minor | patch | build>');
      }

      //reset current branch
      console.log('- Performing reset'.green);
      yield spawn('git', ['reset', '--hard']).progress(printProgress);

      //checkout stable branch
      console.log(`- Checking out ${STABLE_BRANCH}`.green);
      yield spawn('git', ['checkout', STABLE_BRANCH]).progress(printProgress);

      //pull any changes to stable branch
      console.log(`- Checking out ${STABLE_BRANCH}`.green);
      yield spawn('git', ['pull', 'origin', STABLE_BRANCH]).progress(printProgress);

      //checkout the dist branch
      console.log(`- Checking out ${DIST_BRANCH}`.green);
      yield spawn('git', ['checkout', DIST_BRANCH]).progress(printProgress);

      //reset it to the origin version
      console.log(`- Checking out ${DIST_BRANCH}`.green);
      yield spawn('git', ['reset', '--hard', `origin/${DIST_BRANCH}`]).progress(printProgress);

      //merge in stable branch
      console.log(`- Merging ${STABLE_BRANCH}`.green);
      yield spawn('git', ['merge', STABLE_BRANCH]).progress(printProgress);

      //run the build
      console.log('- Running build'.green);
      yield spawn('npm', ['run', 'build']).progress(printProgress);

      //remove ignored built files
      let ignoreFilePath = path.resolve(__dirname, '../../.gitignore');

      if (!fs.existsSync(ignoreFilePath)) {
        throw new Error('No .gitignore file');
      }

      let outputFile = path.resolve(__dirname, `../../styles/${OUTPUT_CSS_FILENAME}`);
      if (fs.existsSync(outputFile)) {
        console.log(`- Deleting output file ${outputFile}`.green);
        yield pUnlink(outputFile);
      }

      //get all css file data
      let cssFilePattern = path.resolve(__dirname, '../../styles/**/*.css');
      let cssFiles = yield pGlob(cssFilePattern);
      let cssData = yield Q.all(cssFiles.map((filePath)=> pReadFile(filePath, 'utf8')));

      //format it
      cssData = cssData.reduce((last, current) => last += current, '');

      console.log('- Writing output css'.green);
      yield pWriteFile(outputFile, cssData, 'utf8');


      console.log('- Removing built files from ignore list'.green);
      let ignoreFileData = yield pReadFile(ignoreFilePath, 'utf8');
      //remove the ignored style folder
      ignoreFileData = ignoreFileData.replace(/^styles\/\*$/g, '');
      yield pWriteFile(ignoreFilePath, ignoreFileData, 'utf8');

      console.log(`- Commiting built files`.green);
      yield spawn('git', ['add', '--all', '.']).progress(printProgress);
      yield spawn('git', ['commit', '--message', 'Update dist files']).progress(printProgress);

      console.log('- Bumping version number'.green);
      yield pUpdate({
        version: releaseType,
        commitMessage: 'Bump version to %s',
        tagName: 'v%s'
      });

      console.log(`- Checking out ${STABLE_BRANCH}`.green);
      yield spawn('git', ['checkout', STABLE_BRANCH]).progress(printProgress);

      console.log('----------------------'.green);
      console.log('Release complete, please run: git push && git push --tags'.green);
      console.log('----------------------'.green);

      process.exit();

    }
    catch (e){
      handleError(e);
    }

  })().done();
};

