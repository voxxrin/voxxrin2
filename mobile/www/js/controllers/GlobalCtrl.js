'use strict';

angular.module('voxxrin')
    .controller('GlobalCtrl', function ($scope) {

        angular.extend($scope, {
            formats: {
                fullDateTime: 'dd/MM/yy - HH:mm'
            },
            platform: {
                uuid: '-'
            }
        });

        document.addEventListener("deviceready", function () {
            $scope.platform.uuid = device.uuid;
        }, false);

    });