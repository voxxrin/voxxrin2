angular.module('voxxrin')
    .controller('IntroCtrl', function ($scope, $rootScope, $state, $localstorage) {

        $scope.slides = [{}];
        $rootScope.introHasBeenShown = true;
        $scope.skipLater = $localstorage.getObject('intro.skip');
        if ($scope.skipLater === null) {
            $scope.skipLater = false;
        }

        $scope.skip = function () {
            $state.go('login');
        };

        $scope.toggleSkipLater = function () {
            $scope.skipLater = !$scope.skipLater;
            $localstorage.setObject('intro.skip', $scope.skipLater);
        };
    });
