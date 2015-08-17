'use strict';

angular.module('voxxrin')
    .controller('GlobalCtrl', function ($scope, $auth, Session) {

        angular.extend($scope, {
            formats: {
                fullDateTime: 'dd/MM/yy - HH:mm'
            },
            platform: {
                uuid: null
            },
            Session: Session
        });

        document.addEventListener("deviceready", function () {
            $scope.platform.uuid = device.uuid;
        }, false);

        $auth.validateUser();
    });