import angular from 'angular';
import box from '../box/index';

var pageLabel = angular.module('guPageLabel', ['guBox']);

pageLabel.directive('guPageLabel', function pageLabelDirective(){
  return {
    restrict: 'E',
    transclude: true,
    replace: true,
    scope: true,
    template: (el, attrs) => `<gu-box variant="tertiary" class="page-label" ng-class="{ open: isOpen }" ng-mouseover="isOpen = true", ng-mouseleave="isOpen = false">
      <div class="page-label__content" ng-transclude></div>
    </gu-box>`
  };
});

pageLabel.directive('guPageLabelHover', function pageLabelHoverDirective(){
  return {
    restrict: 'E',
    transclude: true,
    replace: true,
    template: `<gu-box class="page-label__hover" variant="tertiary" ng-transclude></gu-box>`
  }
})

export default pageLabel;
