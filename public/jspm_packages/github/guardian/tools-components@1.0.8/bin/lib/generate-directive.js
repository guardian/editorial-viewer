import Q      from 'q';
import path   from 'path';
import fs     from 'fs';
import mkdirp from 'mkdirp';
import _      from 'lodash';
import capitalizeFirstChar from 'capitalize-first-char';

let pMkdirP    = Q.denodeify(mkdirp);
let pReadFile  = Q.denodeify(fs.readFile);
let pWriteFile = Q.denodeify(fs.writeFile);

module.exports = (componentName, directiveName) => {
  return Q.async(function* (){
    try{

      console.log(`Generating lib/${componentName}/index.js`.green);

      let directiveDirPath = path.resolve(__dirname, `../../lib/${componentName}`);

      //check if we already have a component
      if (fs.existsSync(directiveDirPath)) {
        console.log(`${directiveDirPath} exists`.red);
        process.exit(1);
      }

      //make the component directory
      yield pMkdirP(directiveDirPath);

      //get the template
      let templateFilePath = path.resolve(__dirname, '../templates/directive.tmpl');
      let templateData = yield pReadFile(templateFilePath, 'utf8');
      let template = _.template(templateData);
      //render the template
      template = template({
        componentName: componentName,
        directiveName: capitalizeFirstChar(componentName)
      });


      let directiveFilePath = path.resolve(directiveDirPath, 'index.js');
      yield pWriteFile(directiveFilePath, template, 'utf8');

      console.log(`${directiveFilePath} created`.green);
    }
    catch (e) {
      console.log('-----------------------');
      console.log(e);
      console.log('-----------------------');
      process.exit(1);
    }


  })().done();
}
