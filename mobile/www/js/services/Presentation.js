'use strict';

angular.module('voxxrin')
    .service('Presentation', function (configuration, $resource) {

        var presentation = $resource(configuration.backendUrl + '/api/presentations/:id');
        var dayPresentations = $resource(configuration.backendUrl + '/api/days/:dayId/presentations');
        var eventPresentations = $resource(configuration.backendUrl + '/api/events/:eventId/presentations');

        return angular.extend(presentation, {
            fromDay: function (dayId) {
                return dayPresentations.query({dayId: dayId});
            },
            fromEvent: function (eventId) {
                return eventPresentations.query({eventId: eventId});
            }
        });
    });