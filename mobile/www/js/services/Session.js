'use strict';

angular.module('voxxrin')
    .service('Session', function ($rootScope, $resource, $log, ServerUrl) {

        var resource = $resource(ServerUrl + '/api/auth/:id');

        var _current = null;
        return {
            isAuthenticated: function () {
                return _current && _current != null;
            },
            getCurrent: function () {
                return _current;
            },
            getPrincipal: function () {
                return _current.principal;
            },
            setCurrent: function (session) {

                _current = session;
                _current.principal = resource.get({id: 'current'}).$promise;
                session.isAuthenticated = true;

                Ionic.io();
                var ioUser = Ionic.User.current();

                if (!ioUser.id) {
                    ioUser.id = session._id;
                }

                ioUser.set('user_id', session._id);
                ioUser.set('name', session.name);
                ioUser.set('image', session.avatarUrl);
                ioUser.set('twitterId', session.twitterId);
                ioUser.save();

                $log.info('Ionic user identified & saved', ioUser);
                $rootScope.$broadcast('ionicUser:identified', ioUser);
            },
            destroy: function () {
                var destroyed = JSON.stringify(_current);
                _current = null;
                console.log('current session destroyed', JSON.parse(destroyed));
            }
        };
    });