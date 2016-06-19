'use strict';

angular.module('voxxrin')
    .filter('twitterId', function () {
        return function (input) {
            if (input) {
                return input[0] === '@' ? input : ('@' + input);
            }
            return input;
        };
    });