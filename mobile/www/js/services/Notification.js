'use strict';

angular.module('voxxrin')
    .service('Notification', function () {

        var _popup = function (title, text, level) {
            swal(title, text, level);
        };

        return {
            popup: {
                success: function (title, text) {
                    _popup(title, text, 'success')
                },
                warning: function () {
                    _popup(title, text, 'warning')
                },
                error: function () {
                    _popup(title, text, 'error')
                }
            }
        }
    });