import angular from 'angular';

var loadingBars = angular.module('guLoadingBars', []);

loadingBars.directive('guLoadingBars', function loadingBarsDirective(){
  return {
    restrict: 'E',
    replace: true,
    template: `<div class="loading-bars">
      <div class="rect1"></div>
      <div class="rect2"></div>
      <div class="rect3"></div>
      <div class="rect4"></div>
      <div class="rect5"></div>
    </div>`
  }
});

export default loadingBars;
