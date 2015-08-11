import angular from 'angular';

var AccordionCtrlMod = angular.module('AccordionCtrlMod', []);

AccordionCtrlMod.controller('AccordionCtrl', [
  '$scope',
  '$element',
  function($scope, $element){

    this.numOfChildren = 0;
    var $track = $element.find('ng-transclude');

    this.setActive = (index) => {
      var rule = `translateX(-${(100 / this.numOfChildren) * index}%)`;
      $track.css({ transform: rule });
    }
  }
]);

export default AccordionCtrlMod;
