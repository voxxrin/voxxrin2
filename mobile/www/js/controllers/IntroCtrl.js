angular.module('voxxrin')
    .controller('IntroCtrl', function ($scope, $state, $localstorage) {

        $scope.slides = [{}];

        $scope.skip = function () {
            $state.go('login');
        };

        $scope.toggleSkipLater = function () {
            $scope.skipLater = !$scope.skipLater;
            $localstorage.setObject('intro.skip', $scope.skipLater);
        };

        $scope.skipLater = $localstorage.getObject('intro.skip', false);
        if ($scope.skipLater === true) {
            $scope.skip();
        }
    });
