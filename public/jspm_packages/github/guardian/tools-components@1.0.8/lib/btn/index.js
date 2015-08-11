import angular from 'angular';

var btn = angular.module('guBtn', []);

var templates = {
  'anchor': '<a class="{{className}}" ng-transclude=true></a>',
  'button': '<button class="{{className}}" ng-transclude=true></button>',
}

btn.directive('guBtn', function btnDirective(){
  return {
    restrict: 'E',
    replace: true,
    transclude: true,
    template: function (el, attrs) {
      return attrs.href ? templates.anchor : templates.button;
    },
    scope: {
      variant: "@",
      size: "@"
    },
    link: function (scope, el, attrs) {
      var className = 'btn';
      if (scope.variant) {
        className = `btn--${scope.variant}`;
      }
      if (scope.size) {
        className += ` btn--${scope.size}`;
      }
      scope.className = className;
    }
  };
});

export default btn;
