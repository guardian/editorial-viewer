import path         from 'path';
import fs           from 'fs';
import Q            from 'q';
import glob         from 'glob';
import mkdirp       from 'mkdirp';
import sass         from 'node-sass';
import autoprefixer from 'autoprefixer';


let pGlob       = Q.denodeify(glob);
let pReadFile   = Q.denodeify(fs.readFile);
let pWriteFile  = Q.denodeify(fs.writeFile);
let pMkdirP     = Q.denodeify(mkdirp);
let pRenderSass = Q.denodeify(sass.render);

export default () => {

  //Q.async takes a generator
  //when the generator yields a promise Q will wait for the promise to resolve
  //and then call generator.next()
  return Q.async(function* (){
    try {

      //the meat and gravy of the operation

      //get all the sass file paths
      let sassPath  = path.resolve(__dirname, '../../sass/components/**/*.scss');
      let sassFiles = yield pGlob(sassPath);

      //replace the first occurrence of scss with css giving us an output path
      let outputFiles = sassFiles.map((sassFilePath)=> sassFilePath.replace(/sass/, 'styles'));

      //get all the file contents
      let sassData = yield Q.all(sassFiles.map((sassFilePath)=> pReadFile(sassFilePath, 'utf8')));

      //render all the file contents into css
      let cssData  = yield Q.all(sassData.map((sassContent)=> pRenderSass({
        data: sassContent,
        includePaths: ['sass/']
      })));

      //prefix all the css
      cssData = cssData.map((data)=> autoprefixer.process(data.css.toString()).css);

      //build our objects for rendering to css files
      let outputs = sassFiles.reduce((last, sassFilePath, index)=> {
        last.push({
          //replace the output .scss to .css
          path: outputFiles[index].replace(/scss$/, 'css'),
          data: cssData[index]
        });
        return last;
      }, []);

      //make sure all the nested directories have been created
      yield Q.all(outputs.map((output)=> {
        let directoryPath = output.path.split('/');
        directoryPath = directoryPath.slice(0, directoryPath.length - 1).join('/');
        return pMkdirP(directoryPath);
      }));

      //write all those files
      yield Q.all(outputs.map((output) => pWriteFile(output.path, output.data, 'utf8')));

      //we are done, lets get outta here!
      console.log('-----------------------');
      console.log('css rendered correctly');
      console.log('-----------------------');
      process.exit(0);
    }
    catch(e){
      console.log('-----------------------');
      console.log(e);
      console.log('-----------------------');
      process.exit(1);
    }
  })().done();
}
