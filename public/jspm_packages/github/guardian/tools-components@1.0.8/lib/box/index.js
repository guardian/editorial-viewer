import angular from 'angular';

var box = angular.module('guBox', []);

box.directive('guBox', function boxDirective(){
  return {
    restrict: 'AE',
    link: function(scope, el, attrs) {
      //restrict the api so you can only use:
      //<gu-box variant="primary"</gu-box>
      //or
      //<div gu-box="primary"</div>
      var boxType = el[0].tagName === 'GU-BOX' ? attrs.variant : attrs.guBox;
      var className = `box--${boxType}`;
      el.addClass(className);
    }
  };
});

export default box;
