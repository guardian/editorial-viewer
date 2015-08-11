//SAFE APPLY
//CHECKS THE CURRECT CYCLE AND RUNS THE FUNCTION ACCORDINGLY
export default ($scope, fn) => {
  if (!$scope || !fn) {
    return;
  }

  const phase = $scope.$root.$$phase;
  if (phase === '$apply' || phase === '$digest' ) {
    fn();
  }
  else {
    $scope.$apply(fn);
  }
}
