'use strict';

angular.module('voxxrin')
    .factory('MainHttpRequestInterceptor', function ($q, $rootScope) {

        var ignoredUrls = [/.*\/api\/ratings.*/, /.*templates\/.*/, /.*ionic\.io.*/ ];
        var nbRequests = 0;

        var isIgnored = function (url) {
            return _.some(ignoredUrls, function (ignored) {
                return url.match(ignored) != null;
            });
        };

        var addRequest = function () {

            if (nbRequests === 0) {
                $rootScope.$broadcast('loading:show');
            }

            nbRequests++;
        };

        var dropRequest = function () {

            nbRequests--;

            if (nbRequests <= 0) {
                nbRequests = 0;
                $rootScope.$broadcast('loading:hide');
            }
        };

        return {

            'request': function (config) {

                if (!isIgnored(config.url)) {
                    addRequest();
                }

                return config || $q.when(config);
            },

            'response': function (response) {

                if (!isIgnored(response.config.url)) {
                    dropRequest();
                }

                return response || $q.when(response);
            },

            'responseError': function (rejection) {

                if (!isIgnored(rejection.config.url)) {
                    dropRequest();
                }

                return $q.reject(rejection);
            }
        };
    });