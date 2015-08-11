//vendor
import angular    from 'angular';
import router     from 'angular-route/angular-route';

//config
import routeData  from './config/routes.json!';

//app
import components from '../lib/gu-components';
import directives from './directives/index';

var app = angular.module('gu-components-app', [
  'ngRoute',
  'guComponents',
  'appDirectives',
]);

app.config(['$routeProvider', function($routeProvider){

  routeData.forEach((data)=>{
    $routeProvider.when(data.slug, {
      templateUrl: data.template,
      controller: data.ctrl
    })
  })

  $routeProvider.otherwise({
    redirectTo: '/'
  });

}]);

