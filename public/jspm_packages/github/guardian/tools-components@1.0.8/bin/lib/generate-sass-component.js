import Q      from 'q';
import fs     from 'fs';
import path   from 'path';
import _      from 'lodash';
import colors from 'colors';
import mkdirp from 'mkdirp';

let pReadFile   = Q.denodeify(fs.readFile);
let pWriteFile  = Q.denodeify(fs.writeFile);
let pMkdirP     = Q.denodeify(mkdirp);

export default (componentName, mixinName) => {

  return Q.async(function* (){
    try {

      //tell the user whats going on
      console.log(`Generating sass/components/${componentName}.scss`.green);

      //if the component exists ...bail out
      let componentFileName = path.resolve(__dirname, '../../sass/components/', componentName + '.scss');
      if (fs.existsSync(componentFileName)) {
        console.log(`${componentFileName} already exists`);
        process.exit(1);
      }

      //get the template
      let templatePath = path.resolve(__dirname, '../templates/sass-component.tmpl');
      let template = yield pReadFile(templatePath, 'utf8');

      //get the file template
      template = _.template(template);
      let fileData = template({
        mixinName: mixinName,
        componentName: componentName
      });

      yield pWriteFile(componentFileName, fileData, 'utf8');
      console.log(`${componentFileName} created`.green);

    }
    catch(e){
      console.log('-----------------------');
      console.log(e);
      console.log('-----------------------');
      process.exit(1);
    }

  })().done();

}
