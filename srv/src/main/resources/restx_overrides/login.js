'use strict';

angular.module('admin').config(function ($httpProvider) {
    var token = localStorage.getItem("restx_token");
    if (token) {
        $httpProvider.defaults.headers.common['token-type'] =  "Bearer";
        $httpProvider.defaults.headers.common['access-token'] =  token;
    }
});

angular.module('admin').factory('baseUri', function () {
    return document.location.href.replace(/^https?:\/\/[^\/]+\//, '/').replace(/\/@.*/, '');
});

angular.module('admin').controller('LoginController', function ($scope, baseUri, $http, $window) {
    $scope.authenticate = function () {
        $http.post(baseUri + '/auth/login', {login: $scope.username, password: SparkMD5.hash($scope.password)})
            .success(function (data, status, headers, config) {
                console.log('authenticated', data, status, headers['access-token']);
                $window.localStorage["restx_token"] = headers('access-token');
                window.location = $.querystring('backTo') || (baseUri + '/@/ui/');
            }).error(function (data, status, headers, config) {
                console.log('error', data, status);
                alertify.error("Authentication error, please try again.");
            });
        ;
    }
});

angular.module('admin').directive('formAutofillFix', function ($timeout) {
    return function (scope, elem, attrs) {
        // Fix autofill issues where Angular doesn't know about autofilled inputs
        if (attrs.ngSubmit) {
            $timeout(function () {
                elem.unbind('submit').submit(function (e) {
                    e.preventDefault();
                    elem.find('input, textarea, select').trigger('input').trigger('change').trigger('keydown');
                    scope.$apply(attrs.ngSubmit);
                });
            });
        }
    };
});
