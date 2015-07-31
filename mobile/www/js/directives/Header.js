'use strict';

angular.module('voxxrin')
    .directive('header', function () {

        return {
            restrict: 'E',
            scope: {
                event: '=',
                title: '@'
            },
            templateUrl: 'templates/header.html'
        }

    });