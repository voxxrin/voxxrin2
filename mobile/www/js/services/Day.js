'use strict';

angular.module('voxxrin')
    .service('Day', function (configuration, $resource) {

        var day = $resource(configuration.backendUrl + '/api/days/:id');
        var eventDays = $resource(configuration.backendUrl + '/api/events/:eventId/days');

        return angular.extend(day, {
            fromEvent: function (eventId) {
                return eventDays.query({eventId: eventId});
            }
        });
    });