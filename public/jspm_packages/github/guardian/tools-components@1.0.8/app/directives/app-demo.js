import angular  from 'angular';
import beautify from 'js-beautify';

var beautifyHTML = beautify.html;

var directivesModule = angular.module('appDemo', []);

directivesModule.directive('appDemo', function(){
  return {
    restrict: 'E',
    transclude: true,
    replace: true,
    template: (el, attrs)=> [
      '<div>',
      '<ng-transclude></ng-transclude>',
      '</div>'
    ].join('')
  }
})

directivesModule.directive('appDemoCode', ['$compile', function($compile){
  return {
    restrict: 'E',
    replace: true,
    terminal: true,
    link: function(scope, el, attrs, transclude) {

      var oldContent = el[0].innerHTML;
      var codeContent = beautifyHTML(oldContent, {indentInnerHtml: true});

      el[0].innerHTML = [
        '<div>',
        oldContent,
        '<pre><code>{{ codeContent }}</code></pre>',
        '{{initHighlight()}}',
        '</div>'
      ].join('');

      $compile(el.contents())(scope);
      scope.codeContent = codeContent;
      //this is total filth
      scope.initHighlight = function(){
        hljs.highlightBlock(el.find('pre')[0]);
      }
    }
  }
}]);

export default directivesModule;
