import angular from 'angular';

class BaseModel {
  constructor(data = {}){
    //ensure we have a defaults object to work with
    this.defaults = this.defauts || {};
    //create our dataset
    this.data = angular.extend({}, this.defaults, data);
  }

  get(key){
    return this.data[key];
  }

  set(key, val){
    //TODO JP 13/4/15 ADD VALIDATION STEP
    this.data[key] = val;
  }
}

export default BaseModel;
