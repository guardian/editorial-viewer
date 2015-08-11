import angular from 'angular';

class BaseCollection {
  constructor(models = []){
    //parse and sort models
    if (this.model) {
      this.models = models.map((model)=> new this.model(model)).sort(this.comparator);
    } else {
      this.models = models.sort(this.comparator);
    }
  }

  comparator(){
    //write your own
  }

  getModels(){
    return this.models;
  }

  getModelAt(index){
    return this.models[index];
  }

  find(predicate){
    return this.models.find((model)=> predicate.call(model, model.data));
  }

  indexOf(model){
    return this.models.indexOf(model);
  }

  length(){
    return this.models.length;
  }

}

export default BaseCollection;
