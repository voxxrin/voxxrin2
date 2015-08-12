'use strict';

angular.module('voxxrin')
    .filter('untrustedHtml', function ($sanitize) {
        return function (text) {
            return $sanitize(text);
        };
    });