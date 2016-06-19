'use strict';

angular.module('voxxrin')
    .filter('trustedHtml', function ($sce) {
        return function (text) {
            return $sce.trustAsHtml(text);
        };
    });