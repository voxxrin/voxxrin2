'use strict';

angular.module('voxxrin')
    .controller('LoginCtrl', function ($scope, $auth, $ionicPopup) {

        $scope.authenticate = function (provider) {
            $auth.authenticate(provider).then(function (result) {
                $ionicPopup.alert({
                    title: 'Success',
                    content: 'You have successfully logged in!' + JSON.stringify(result)
                });
            }).catch(function (response) {
                $ionicPopup.alert({
                    title: 'Error',
                    content: response.data ? JSON.stringify(response.data) : JSON.stringify(response)
                })
            });
        };
    });