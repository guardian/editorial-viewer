import angular from 'angular'

import clockActive      from './svg/clock-active.svg!';
import clockDisabled    from './svg/clock-disabled.svg!';
import infoActive       from './svg/info-active.svg!';
import infoDisabled     from './svg/info-disabled.svg!';
import publishActive    from './svg/publish-active.svg!';
import publishDisabled  from './svg/publish-disabled.svg!';
import previewActive    from './svg/preview-active.svg!';
import arrowDown        from './svg/arrow-down.svg!';
import composerIcon     from './svg/composer-icon.svg!';
import wrenchActive     from './svg/wrench-active.svg!';
import wrenchDisabled   from './svg/wrench-disabled.svg!';
import expandActive     from './svg/expand-active.svg!';
import expandDisabled   from './svg/expand-disabled.svg!';

var templates = {
  'clock-active'      : clockActive,
  'clock-disabled'    : clockDisabled,
  'info-active'       : infoActive,
  'info-disabled'     : infoDisabled,
  'publish-active'    : publishActive,
  'publish-disabled'  : publishDisabled,
  'preview-active'    : previewActive,
  'arrow-down'        : arrowDown,
  'composer-icon'     : composerIcon,
  'wrench-active'     : wrenchActive,
  'wrench-disabled'   : wrenchDisabled,
  'expand-active'     : expandActive,
  'expand-disabled'   : expandDisabled
}

var icons = angular.module('guIcons', []);

icons.directive('guIcon', function icons(){
  return {
    restrict: 'E',
    template: (el, attrs) => templates[attrs.variant].outerHTML
  };
});

export default icons;
