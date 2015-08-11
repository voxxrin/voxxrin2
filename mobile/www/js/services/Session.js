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
                _current = session;
            }
        };
    });