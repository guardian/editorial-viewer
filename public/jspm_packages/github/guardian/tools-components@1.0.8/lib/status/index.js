import angular from 'angular';

var status = angular.module('guStatus', []);

status.directive('guStatus', function statusDirective(){
  return {
    restrict: 'E',
    replace: true,
    transclude: true,
    template: (el, attrs) => {
      var tag = attrs.variant || "default";
      return `<div class="status--${tag}" ng-transclude></div>`
    }
  };
});

export default status;
