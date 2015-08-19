'use strict';

angular.module('voxxrin')
    .controller('GlobalCtrl', function ($scope, $auth, Session, $ionicSideMenuDelegate) {

        angular.extend($scope, {
            formats: {
                fullDateTime: 'dd/MM/yy - HH:mm'
            },
            platform: {
                uuid: null
            },
            Session: Session,
            toggleLeft: function() {
                $ionicSideMenuDelegate.toggleLeft();
            }
        });

        document.addEventListener("deviceready", function () {
            $scope.platform.uuid = device.uuid;
        }, false);

        $auth.validateUser();
    });