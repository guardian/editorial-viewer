import angular from 'angular';
import AccordionCtrlMod from './AccordionCtrl';

var accordion = angular.module('guAccordion', ['AccordionCtrlMod']);

accordion.directive('guAccordion', function accordionDirective(){
  return {
    restrict: 'E',
    replace: true,
    transclude: true,
    template: `<div class="accordion">
      <ng-transclude class="accordion__track"
                     ng-style="{ width: trackWidth }">
      </ng-transclude>
    </div>`,
    controller: 'AccordionCtrl',
    compile: function(){
      return {
        post: function($scope, el, attrs, ctrl){
          var accordionChildren = el.find('ng-transclude').find('gu-accordion-child');
          var numOfChildren = ctrl.numOfChildren = accordionChildren.length;
          var trackWidth = numOfChildren * 100;
          var childWidth = 100 / numOfChildren;
          childWidth += '%';

          //setup the scope
          $scope.trackWidth = trackWidth += '%';
          $scope.numOfChildren = numOfChildren;

          //set widths on all children
          Array.prototype.slice.call(accordionChildren)
            .forEach((el, index)=>{
              var $el = angular.element(el);
              $el.css({ width: childWidth });
              $el.attr('index', index);
            });
        }
      }
    }
  };
});

accordion.directive('guAccordionChild', function(){
  return {
    restrict: 'E',
    require: '^guAccordion',
    scope: {
      when: '@'
    },
    link: function(scope, el, attrs, ctrl){
      scope.$watch('when', function(val){
        if (val === 'true' || val === true) {
          ctrl.setActive(el.attr('index'));
        }
      });
    }
  }
});

export default accordion;
