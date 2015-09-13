'use strict';

angular.module('voxxrin')
    .service('Session', function ($rootScope, $ionicUser) {

        var _current = null;
        return {
            isAuthenticated: function () {
                return _current && _current != null;
            },
            getCurrent: function () {
                return _current;
            },
            setCurrent: function (session) {
                console.log('current session', session);
                _current = session;
                $ionicUser.identify({
                    user_id: session._id,
                    name: session.name,
                    image: session.avatarUrl,
                    twitterId: session.twitterId
                }).then(function () {
                    $rootScope.$broadcast('ionicUser:identified');
                });
            },
            destroy: function () {
                var destroyed = JSON.stringify(_current);
                _current = null;
                console.log('current session destroyed', JSON.parse(destroyed));
            }
        };
    });