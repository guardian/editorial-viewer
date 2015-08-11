import Q    from 'q';
import path from 'path';
import fs   from 'fs';
import _    from 'lodash';

let pReadFile  = Q.denodeify(fs.readFile);
let pWriteFile = Q.denodeify(fs.writeFile)

module.exports = (componentName) => {
  return Q.async(function* (){

    try{

    console.log(`Generating ./${componentName}.js`.green);

    let bootstrapFilePath = path.resolve(__dirname, `../../${componentName}.js`);

    if (fs.existsSync(bootstrapFilePath)) {
      console.log(`${bootstrapFilePath} already exists`.red);
      process.exit(1);
    }

    let templatePath = path.resolve(__dirname, '../templates/bootstrap.tmpl');
    let templateData = yield pReadFile(templatePath, 'utf8');
    let template = _.template(templateData);
    template = template({
      componentName: componentName
    });

    let bootstrapOutputFilePath = path.resolve(__dirname, `../../${componentName}.js`);
    yield pWriteFile(bootstrapOutputFilePath, template, 'utf8');
    console.log(`${bootstrapFilePath} created`.green);

    }
    catch (e) {
      console.log('-----------------------');
      console.log(e);
      console.log('-----------------------');
      process.exit(1);
    }

  })().done();
}
