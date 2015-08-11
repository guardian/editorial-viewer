import angular from 'angular';
import safeApply from '../utils/safe-apply';

const ESC = 27;

var ModalCtrlMod = angular.module('ModalCtrlMod', []);

var ModalCtrl = ModalCtrlMod.controller('ModalCtrl', [
  '$scope',
  '$attrs',
  function($scope, attrs){

    window.addEventListener('keydown', function(e){
      if ($scope.when && e.keyCode === ESC) {
        safeApply($scope, ()=> $scope.when = false);
      }
    });

  }
]);

export default ModalCtrlMod;
