'use strict';

angular.module('voxxrin')
    .service('Day', function ($resource, ServerUrl) {

        var day = $resource(ServerUrl + '/api/days/:id');
        var eventDays = $resource(ServerUrl + '/api/events/:eventId/days');

        return angular.extend(day, {
            fromEvent: function (eventId) {
                return eventDays.query({eventId: eventId});
            }
        });
    });