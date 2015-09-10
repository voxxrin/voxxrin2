'use strict';

angular.module('voxxrin')
    .filter('speakers', function () {
        return function (speakers) {
            var display = [];
            _.each(speakers, function (speaker) {
                display.push(speaker.firstName + ' ' + speaker.lastName);
            });
            return display.join(', ');
        };
    })
    .filter('companies', function () {
        return function (speakers) {
            var display = [];
            _.each(speakers, function (speaker) {
                display.push(speaker.company);
            });
            return display.join(', ');
        };
    });