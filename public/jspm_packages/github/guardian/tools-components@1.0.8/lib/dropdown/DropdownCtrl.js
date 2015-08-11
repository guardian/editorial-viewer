import angular from 'angular';

var DropdownCtrlMod = angular.module('guDropdownCtrl', []);

var DropdownCtrl = DropdownCtrlMod.controller('guDropdownCtrl', [
  '$scope',
  function DropdownCtrl(scope){

    scope.isOpen = (this.state === 'open');

    this.onTriggerClicked = function onTriggerClicked() {
      //toggle state
      scope.isOpen = !scope.isOpen;
    }
  }
]);

export default DropdownCtrlMod;
