'use strict';

angular.module('voxxrin')
    .controller('LoginCtrl', function ($scope, $rootScope, $state, $auth, Notification, Session) {

        angular.extend($scope, {
            Session: Session,
            isAuthenticated: false,
            authenticate: function (provider) {
                $auth.authenticate(provider).then(function () {
                    console.log('Social authentication success !');
                }).catch(function () {
                    Notification.popup.error('Error', 'Error during the social authentication...');
                });
            },
            logout: function () {
                $auth.signOut();
            }
        });

        $rootScope.$on('auth:validation-success', function (event, user) {
            $scope.isAuthenticated = true;
        });

        $rootScope.$on('auth:login-success', function (event, user) {
            $scope.isAuthenticated = true;
        });

        $rootScope.$on('auth:logout-success', function () {
            $scope.isAuthenticated = false;
        });
    });