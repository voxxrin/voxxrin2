'use strict';

angular.module('voxxrin')
    .controller('GlobalCtrl', function ($scope) {

        angular.extend($scope, {
            formats: {
                fullDateTime: 'dd/MM/yy - HH:mm'
            }
        });

    });