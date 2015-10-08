'use strict';

angular.module('voxxrin')
    .filter('speaker', function () {
        return function (speaker) {
            return speaker.firstName + ' ' + speaker.lastName;
        };
    })
    .filter('speakers', function () {
        return function (speakers) {
            var display = [];
            _.each(speakers, function (speaker) {
                var identity = speaker.firstName + ' ' + speaker.lastName;
                var company = speaker.company ? ' (' + speaker.company + ')' : '';
                display.push(identity + company);
            });
            return display.join(', ');
        };
    });