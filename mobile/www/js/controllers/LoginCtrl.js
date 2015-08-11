'use strict';

angular.module('voxxrin')
    .controller('LoginCtrl', function ($scope, $state, $auth, $ionicPopup, Session) {

        angular.extend($scope, {
            Session: Session,
            authenticate: function (provider) {
                $auth.authenticate(provider).then(function (result) {
                    console.log('Social authentication success', result);
                    Session.setCurrent(result);
                    $state.go('events');
                }).catch(function () {
                    $ionicPopup.alert({
                        title: 'Error',
                        content: 'Error during the social authentication...'
                    })
                });
            }
        });
    });