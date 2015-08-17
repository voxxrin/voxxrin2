'use strict';

angular.module('voxxrin')
    .service('Session', function () {

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
            },
            destroy: function () {
                var destroyed = JSON.stringify(_current);
                _current = null;
                console.log('current session destroyed', JSON.parse(destroyed));
            }
        };
    });