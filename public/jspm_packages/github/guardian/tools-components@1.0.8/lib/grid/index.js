import angular from 'angular';

var grid = angular.module('guGrid', []);

// COLUMN
// ------------------------
grid.directive('guColumn', function columnDirective(){
  return {
    restrict: 'E',
    link: (scope, el, attrs)=> {
      var className = `col-${attrs.span}`;
      el.addClass(className);
    }
  };
});


// ROW
// ------------------------
grid.directive('guRow', function rowDirective(){
  return {
    restrict: 'E',
    link: (scope, el)=> {
      el.addClass('row');
    }
  };
});


export default grid;
