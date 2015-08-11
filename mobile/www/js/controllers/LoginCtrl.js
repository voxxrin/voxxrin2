'use strict';

angular.module('voxxrin')
    .controller('LoginCtrl', function ($scope, $auth, $ionicPopup, Session) {

        angular.extend($scope, {
            Session: Session,
            authenticate: function (provider) {
                $auth.authenticate(provider).then(function (result) {
                    console.log('Social authentication success', result);
                    Session.setCurrent(result);
                }).catch(function () {
                    $ionicPopup.alert({
                        title: 'Error',
                        content: 'Error during the social authentication...'
                    })
                });
            }
        });
    });