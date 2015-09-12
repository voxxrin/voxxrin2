'use strict';

angular.module('voxxrin')
    .filter('orDash', function () {
        return function (input) {
            if (!input || input === '') {
                return '-';
            } else {
                return input;
            }
        };
    });