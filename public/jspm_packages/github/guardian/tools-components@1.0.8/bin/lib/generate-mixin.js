import Q      from 'q';
import fs     from 'fs';
import path   from 'path';
import _      from 'lodash';
import colors from 'colors';
import mkdirp from 'mkdirp';

let pReadFile   = Q.denodeify(fs.readFile);
let pWriteFile  = Q.denodeify(fs.writeFile);
let pMkdirP     = Q.denodeify(mkdirp);

export default (mixinName, componentName) => {

  return Q.async(function* (){
    try {

      //tell the user whats going on
      console.log(`Generating sass/mixins/${componentName}/${mixinName}.scss`.green);

      //if we dont have a directory to write to... make one.
      let mixinDirName = path.resolve(__dirname, '../../sass/mixins/', componentName);
      if (!fs.existsSync(mixinDirName)) {
        yield pMkdirP(mixinDirName);
      }

      //if the mixin already exists ... bail out
      let mixinFileName = path.resolve(mixinDirName, mixinName + '.scss');
      if (fs.existsSync(mixinFileName)) {
        console.log('Mixin file exists'.red);
        process.exit(1);
      }

      //get the file template
      let templatePath = path.resolve(__dirname, '../templates/mixin.tmpl');
      let template = yield pReadFile(templatePath, 'utf8');
      //compile template
      template = _.template(template);
      let fileData = template({
        mixinName: mixinName,
        componentName: componentName
      });

      yield pWriteFile(mixinFileName, fileData, 'utf8');

      console.log(`${mixinFileName} created`.green);
    }
    catch(e){
      console.log('-----------------------');
      console.log(e);
      console.log('-----------------------');
      process.exit(1);
    }

  })().done();

}
