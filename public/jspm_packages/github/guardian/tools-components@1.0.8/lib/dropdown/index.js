import angular  from 'angular';
import box      from '../box/index';
import icons    from '../icons/index';

import DropdownCtrl from './DropdownCtrl';

var dropdown = angular.module('guDropdown', ['guBox', 'guIcons', 'guDropdownCtrl']);

dropdown.directive('guDropdown', function dropdownDirective(){
  return {
    transclude: true,
    template: '<div class="dropdown" ng-class="{ closed: !isOpen }" ng-transclude></div>',
    restrict: 'E',
    scope: { state: '@' },
    controllerAs: 'ctrl',
    bindToController: true,
    controller:'guDropdownCtrl'
  };
});

dropdown.directive('guDropdownTrigger', function dropdownTriggerDirective(){
  return {
    require: '^guDropdown',
    restrict: 'E',
    replace: true,
    transclude: true,
    template: (el, attrs) => `<gu-box ng-click="onTriggerClicked()" class="dropdown__trigger" variant="primary">
      <ng-transclude></ng-transclude>
      <gu-icon class="dropdown__trigger__icon" variant="${ attrs.icon || 'arrow-down' }"></gu-icon>
    </gu-box>`,
    link: function(scope, el, attrs, ctrl){
      scope.onTriggerClicked = ctrl.onTriggerClicked;
    }
  }
});

dropdown.directive('guDropdownItem', function dropdownItemDirective(){
  return {
    restrict: 'E',
    replace: true,
    transclude: true,
    template: '<gu-box class="dropdown__item" variant="tertiary" ng-transclude></gu-box>'
  }
});

export default dropdown;
